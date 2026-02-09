package com.toycell.serviceaccount.entity;

import com.toycell.commondomain.entity.BaseEntity;
import com.toycell.commonencrypt.converter.EncryptedStringConverter;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "user_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfile extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_profiles_seq_gen")
    @SequenceGenerator(name = "user_profiles_seq_gen", sequenceName = "user_profiles_seq", allocationSize = 1)
    private Long id;

    @Column(nullable = false, unique = true, name = "user_id")
    private Long userId; // Reference to User from service-auth

    @Column(nullable = false, length = 100, name = "first_name")
    private String firstName;

    @Column(nullable = false, length = 100, name = "last_name")
    private String lastName;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "identity_number", length = 500)
    private String identityNumber; // TCKN - encrypted

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "phone_number", length = 500)
    private String phoneNumber; // Encrypted

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "is_verified")
    @Builder.Default
    private Boolean verified = false;
}
