package com.example.Mapper;

import com.example.dto.RegisterUserDto;
import com.example.dto.UserDto;
import com.example.entity.DriveUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper
{
    DriveUser newUser(final RegisterUserDto userDto);
}


