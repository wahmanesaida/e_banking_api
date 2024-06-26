package com.ecommerce.api.Entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Transfert {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "amount_transfer")
    private BigDecimal amount_transfer;

    @Enumerated(EnumType.STRING)
    @Column(name = "type_transfer")
    private TypeTransfer typeOftransfer;

    @Enumerated(EnumType.STRING)
    @Column(name = "typeOfFees")
    private TypeOfFees typeOfFees;

    @Column(name = "amountOfFees")
    private BigDecimal amountOfFees;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private TransferStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "motif")
    private Motif motif;

    @CreationTimestamp
    @Column(name = "createTime")
    private LocalDateTime createTime;

    @Column(name = "transferRef")
    private String transferRef;

    @ManyToOne
    @JoinColumn(name = "ClientId")
    private User client;

    @ManyToOne
    @JoinColumn(name = "AgentId")
    private User agent;

    @ManyToOne
    @JoinColumn(name = "beneficiairy_id")
    private Beneficiary beneficiary;  

    @OneToOne(mappedBy = "transfert")
    @JoinColumn(name = "transfert_id")
    private Transaction transaction;
}
