package com.ecommerce.api.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@Entity
public class CodePin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String username;
    private String codepin;
    @OneToOne
    @JoinColumn(name = "transfer_id")
    private Transfert transfer;
}
