package com.example.Mapper;

import com.example.dto.RegisterUserDto;
import com.example.dto.UserDto;
import com.example.entity.DriveUser;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper
{
    UserMapper MAPPER = (UserMapper) Mappers.getMapper((Class)UserMapper.class);
    UserDto convert(final DriveUser user);
    DriveUser newUser(final RegisterUserDto userDto);
}


