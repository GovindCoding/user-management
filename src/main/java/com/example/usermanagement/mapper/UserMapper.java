package com.example.usermanagement.mapper;

import com.example.usermanagement.dto.UserDto;
import com.example.usermanagement.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface UserMapper {
  UserDto toDto(User user);
  User toEntity(UserDto dto);
  void updateFromDto(UserDto dto, @MappingTarget User entity);
}
