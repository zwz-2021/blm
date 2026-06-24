package com.chimeil.infrastructure.adapter.address;

import com.chimeil.entity.AddressBook;
import com.chimeil.mapper.AddressBookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MybatisAddressBookRepository implements AddressBookRepository {

    @Autowired
    private AddressBookMapper addressBookMapper;

    @Override
    public AddressBook getById(Long id) {
        return addressBookMapper.getById(id);
    }
}
