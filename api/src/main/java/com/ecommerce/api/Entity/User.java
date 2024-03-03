package com.ecommerce.api.Entity;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.validation.constraints.Pattern;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(builderMethodName = "userBuilder")
@Table(name="_user")
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="username", unique = true, nullable = false, length = 100)
    private String username;

    @Column(name="password", nullable = false, length = 70)
    private String password;

    @Column(name="name", nullable = false, length = 70)
    private String name;

    @CreationTimestamp
    @Column(name = "createTime", nullable = false)
    private LocalDateTime createTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private Role role;

    @Enumerated(EnumType.STRING)
    @Column(name = "title")
    private Title title;

    @Enumerated(EnumType.STRING)
    @Column(name = "pieceIdentite")
    private TypePieceIdentite pieceIdentite;

    @Column(name = "paysEmission")
    private String paysEmission = "Maroc";

    @Column(name = "numeroPieceIdentite")
    private String numeroPieceIdentite;

    @Temporal(TemporalType.DATE)
    @Column(name = "expirationPieceIdentite")
    private Date expirationPieceIdentite;

    @Temporal(TemporalType.DATE)
    @Column(name = "validitePieceIdentite")
    private Date validitePieceIdentite;

    @Column(name = "datenaissance")
    private LocalDate datenaissance;

    @Enumerated(EnumType.STRING)
    @Column(name = "profession")
    private  Profession profession;

    @Column(name = "payeNationale")
    private String payeNationale="maroc";

    @Enumerated(EnumType.STRING)
    @Column(name = "ville")
    private Ville ville;

    @Column(name = "GSM")

    @Pattern(regexp = "^\\+212\\d{9}$", message = "Invalid Moroccan phone number")
    private String GSM;

    @Column(name = "account_amount")
    private BigDecimal account_amount;

    @Column(name = "beneficiaryList")
    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Beneficiary> beneficiaryList;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return true;
    }
    public static Object builder() {
        return null;
    }

    
}
