package com.chimeil.infrastructure.adapter.address;

import com.chimeil.entity.AddressBook;
import com.chimeil.mapper.AddressBookMapper;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class MybatisAddressBookRepository implements AddressBookRepository {

        private final AddressBookMapper addressBookMapper;

    @Override
    public AddressBook getById(Long id) {
        return addressBookMapper.getById(id);
    }
}
