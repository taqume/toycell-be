package com.toycell.servicetransaction.mapper;

import com.toycell.servicetransaction.dto.TransactionRequest;
import com.toycell.servicetransaction.dto.TransactionResponse;
import com.toycell.servicetransaction.entity.Transaction;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TransactionMapper {

    Transaction toEntity(TransactionRequest request);

    TransactionResponse toResponse(Transaction transaction);
}
