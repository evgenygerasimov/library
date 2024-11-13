package com.example.library.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDateTime transactionDate;
    private String operation; // "borrow" or "return"
    @ManyToOne
    private Reader reader;
    @ManyToOne
    private Book book;
}
