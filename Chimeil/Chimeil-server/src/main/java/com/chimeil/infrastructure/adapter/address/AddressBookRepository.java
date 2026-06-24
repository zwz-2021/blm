package com.chimeil.infrastructure.adapter.address;

import com.chimeil.entity.AddressBook;

public interface AddressBookRepository {

    AddressBook getById(Long id);
}
