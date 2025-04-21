package org.anas.paymentfraud.storeservice.repository;


import org.anas.paymentfraud.storeservice.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, String> {
}