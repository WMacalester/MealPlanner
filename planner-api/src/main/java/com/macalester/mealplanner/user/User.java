package com.macalester.mealplanner.user;

import com.macalester.mealplanner.validator.UsernameConstraint;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    @UsernameConstraint
    private String username;

    private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    @Override
    public String toString() {
        return username;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof User other)) {
            return false;
        }
        return username.equals(other.username);
    }

    @Override
    public int hashCode() {
        return 113 + (username == null ? 37 : username.hashCode());
    }
}
