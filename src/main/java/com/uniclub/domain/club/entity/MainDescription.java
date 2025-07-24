package com.uniclub.domain.club.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

//필요 없을듯

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "mainDescription")
public class MainDescription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mainDescriptionId;    //PK



}
