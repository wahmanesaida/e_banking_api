package com.ecommerce.api.ManageUsers.Dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.ecommerce.api.Entity.Profession;
import com.ecommerce.api.Entity.Role;
import com.ecommerce.api.Entity.Title;
import com.ecommerce.api.Entity.TypePieceIdentite;
import com.ecommerce.api.Entity.Ville;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {

    private long id;

    private String username;

    private String password;

    private String name;

    private LocalDateTime createTime;

    private Role role;

    private Title title;

    private TypePieceIdentite pieceIdentite;

    private String paysEmission = "Maroc";

    private String numeroPieceIdentite;

    private Date expirationPieceIdentite;

    private Date validitePieceIdentite;

    private LocalDate datenaissance;

    private Profession profession;

    private String payeNationale = "maroc";

    private Ville ville;

    private String GSM;

    private BigDecimal account_amount;

}
