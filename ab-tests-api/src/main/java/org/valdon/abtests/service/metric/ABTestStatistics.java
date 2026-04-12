package org.valdon.abtests.service.metric;

import org.apache.commons.math3.distribution.NormalDistribution;

/**
 * Утилитный класс для вспомогательных расчетов для A/B тестов по бинарным метрикам:
 * CTR, VIEW_RATE, LIKE_RATE, WATCH_START_RATE, WATCH_FINISH_RATE, WATCH_THROUGH_RATE
 */
public final class ABTestStatistics {

    /** Порог значимости: p-value < 0.05 */
    public static final double SIGNIFICANCE_LEVEL = 0.05;

    /** Защита от деления на ноль и нестабильных вычислений */
    private static final double EPSILON = 1e-12;

    /** z-значение для двустороннего 95% доверительного интервала */
    private static final double Z_95 = 1.959963984540054;

    /** Стандартное нормальное распределение */
    private static final NormalDistribution STANDARD_NORMAL = new NormalDistribution();

    private ABTestStatistics() {
    }

    /**
     * Считает p-value для сравнения двух долей через двусторонний z-test.
     *
     * successA / totalA — контроль
     * successB / totalB — тест
     *
     * Возвращает null, если данные некорректны.
     */
    public static Double calculatePValue(
            long successA, long totalA,
            long successB, long totalB
    ) {
        if (!isValidBinomialInput(successA, totalA) || !isValidBinomialInput(successB, totalB)) {
            return null;
        }

        if (totalA == 0 || totalB == 0) {
            return null;
        }

        double p1 = (double) successA / totalA;
        double p2 = (double) successB / totalB;

        double pooled = (double) (successA + successB) / (totalA + totalB);

        double standardError = Math.sqrt(
                pooled * (1.0 - pooled) * ((1.0 / totalA) + (1.0 / totalB))
        );

        if (standardError < EPSILON) {
            return Math.abs(p1 - p2) < EPSILON ? 1.0 : 0.0;
        }

        double z = (p2 - p1) / standardError;
        return 2.0 * (1.0 - STANDARD_NORMAL.cumulativeProbability(Math.abs(z)));
    }

    /** Проверяет, что результат статистически значим */
    public static boolean isSignificant(Double pValue) {
        return pValue != null && pValue < SIGNIFICANCE_LEVEL;
    }

    /**
     * Считает uplift в процентах.
     *
     * Формула:
     * ((test - control) / control) * 100
     */
    public static Double calculateUplift(double controlValue, double testValue) {
        if (Math.abs(controlValue) < EPSILON) {
            return null;
        }
        return ((testValue - controlValue) / controlValue) * 100.0;
    }

    /**
     * Считает абсолютную разницу долей: pB - pA.
     *
     * Например:
     * 0.12 - 0.10 = 0.02 = +2 п.п.
     */
    public static Double calculateAbsoluteDifference(
            long successA, long totalA,
            long successB, long totalB
    ) {
        if (!isValidBinomialInput(successA, totalA) || !isValidBinomialInput(successB, totalB)) {
            return null;
        }

        if (totalA == 0 || totalB == 0) {
            return null;
        }

        double p1 = (double) successA / totalA;
        double p2 = (double) successB / totalB;

        return p2 - p1;
    }

    /**
     * Строит 95% доверительный интервал для разницы долей pB - pA
     * через Newcombe-Wilson.
     */
    public static ConfidenceInterval calculateDifferenceConfidenceInterval95(
            long successA, long totalA,
            long successB, long totalB
    ) {
        if (!isValidBinomialInput(successA, totalA) || !isValidBinomialInput(successB, totalB)) {
            return null;
        }

        if (totalA == 0 || totalB == 0) {
            return null;
        }

        ProportionInterval ciA = calculateWilsonInterval(successA, totalA, Z_95);
        ProportionInterval ciB = calculateWilsonInterval(successB, totalB, Z_95);

        double lower = ciB.lowerBound() - ciA.upperBound();
        double upper = ciB.upperBound() - ciA.lowerBound();

        return new ConfidenceInterval(lower, upper);
    }

    /**
     * Оценивает нужный размер выборки на одну группу
     * для сравнения двух долей.
     *
     * baselineRate — ожидаемая метрика в контроле
     * minDetectableEffectAbs — минимальный абсолютный эффект
     * power — мощность теста
     * alpha — уровень значимости
     */
    public static Long calculateRequiredSampleSizePerGroup(
            double baselineRate,
            double minDetectableEffectAbs,
            double power,
            double alpha
    ) {
        if (baselineRate <= 0.0 || baselineRate >= 1.0) {
            return null;
        }

        if (minDetectableEffectAbs <= 0.0) {
            return null;
        }

        if (power <= 0.0 || power >= 1.0 || alpha <= 0.0 || alpha >= 1.0) {
            return null;
        }

        double p1 = baselineRate;
        double p2 = baselineRate + minDetectableEffectAbs;

        if (p2 <= 0.0 || p2 >= 1.0) {
            return null;
        }

        double zAlpha = STANDARD_NORMAL.inverseCumulativeProbability(1.0 - alpha / 2.0);
        double zBeta = STANDARD_NORMAL.inverseCumulativeProbability(power);

        double pooled = (p1 + p2) / 2.0;

        double numerator =
                zAlpha * Math.sqrt(2.0 * pooled * (1.0 - pooled)) +
                        zBeta * Math.sqrt(p1 * (1.0 - p1) + p2 * (1.0 - p2));

        double n = (numerator * numerator) / ((p2 - p1) * (p2 - p1));

        return (long) Math.ceil(n);
    }

    /** Проверяет, что 0 <= success <= total */
    private static boolean isValidBinomialInput(long success, long total) {
        return total >= 0 && success >= 0 && success <= total;
    }

    /** Wilson-интервал для одной доли */
    private static ProportionInterval calculateWilsonInterval(long success, long total, double z) {
        double n = total;
        double p = (double) success / total;
        double z2 = z * z;

        double denominator = 1.0 + z2 / n;
        double center = (p + z2 / (2.0 * n)) / denominator;

        double margin = (z / denominator) * Math.sqrt(
                (p * (1.0 - p) / n) + (z2 / (4.0 * n * n))
        );

        double lower = Math.max(0.0, center - margin);
        double upper = Math.min(1.0, center + margin);

        return new ProportionInterval(lower, upper);
    }

    /** Доверительный интервал для разницы долей */
    public record ConfidenceInterval(
            double lowerBound,
            double upperBound
    ) { }

    /** Внутренний интервал для одной доли */
    private record ProportionInterval(
            double lowerBound,
            double upperBound
    ) { }

}