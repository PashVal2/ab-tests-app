package org.valdon.abtests.domain.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.proxy.HibernateProxy;
import org.valdon.abtests.domain.role.Role;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id", nullable = false),
            inverseJoinColumns = @JoinColumn(name = "role_id", nullable = false)
    )
    private Set<Role> roles = new HashSet<>();

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy
                ? ((HibernateProxy) this)
                .getHibernateLazyInitializer()
                .getPersistentClass()
                : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this)
                .getHibernateLazyInitializer()
                .getPersistentClass()
                : getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;

        User that = (User) o;
        return getId() != null && getId().equals(that.getId());
    }

    @Override
    public final int hashCode() {
        Class<?> effectiveClass = this instanceof HibernateProxy
                ? ((HibernateProxy) this)
                .getHibernateLazyInitializer()
                .getPersistentClass()
                : getClass();
        return effectiveClass.hashCode();
    }

}
