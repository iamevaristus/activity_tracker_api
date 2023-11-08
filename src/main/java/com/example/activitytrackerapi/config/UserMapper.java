package com.example.activitytrackerapi.config;

import com.example.activitytrackerapi.dto.UserDto;
import com.example.activitytrackerapi.models.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper( UserMapper.class );

    UserDto userToUserDto(User user);
}
