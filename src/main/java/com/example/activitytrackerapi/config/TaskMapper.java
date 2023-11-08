package com.example.activitytrackerapi.config;

import com.example.activitytrackerapi.dto.TaskDto;
import com.example.activitytrackerapi.models.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper( TaskMapper.class );

    TaskDto taskToTaskDto(Task task);
}
