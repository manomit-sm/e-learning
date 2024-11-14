package com.bsolz.elearning.entities;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.UUID;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(schema = "user_service", name = "users")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class User extends DateAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    private String cognitoUserId;
    private String name;
    private String email;
    private String phoneNo;
    private String address;
    private String photo;
    private boolean status;
}
