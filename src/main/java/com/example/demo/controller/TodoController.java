package com.example.demo.controller;

import com.example.demo.dto.ResponseDTO;
import com.example.demo.dto.TodoDTO;
import com.example.demo.model.TodoEntity;
import com.example.demo.service.TodoService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/todo")
public class TodoController {

    @Autowired
    private TodoService service;

    @PostMapping
    public ResponseDTO<TodoDTO> createTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {

        try {
            TodoEntity entity = TodoDTO.toEntity(dto);

            entity.setId(null);
            entity.setUserId(userId);

            List<TodoEntity> entities = service.create(entity);

            // 자바스트림으로 모델 TodoEntity를 TodoDTO로 변환
            List<TodoDTO> dtos = entities.stream()
                    .map(TodoDTO::new)
                    .collect(Collectors.toList());

            return ResponseDTO.<TodoDTO>builder().data(dtos).build();
        } catch (Exception e) {
            String error = e.getMessage();
            return ResponseDTO.<TodoDTO>builder().error(error).build();
        }
    }


    @GetMapping
    public ResponseDTO<TodoDTO> retrieveTodoList(@AuthenticationPrincipal String userId) {

        List<TodoEntity> entities = service.retrieve(userId);

        List<TodoDTO> dtos = entities.stream()
                .map(TodoDTO::new)
                .collect(Collectors.toList());

        return ResponseDTO.<TodoDTO>builder().data(dtos).build();
    }


    @PutMapping
    public ResponseDTO<TodoDTO> updateTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {


        TodoEntity entity = TodoDTO.toEntity(dto);

        entity.setUserId(userId);

        List<TodoEntity> entities = service.update(entity);
        List<TodoDTO> dtos = entities.stream()
                .map(TodoDTO::new)
                .collect(Collectors.toList());

        return ResponseDTO.<TodoDTO>builder().data(dtos).build();
    }

    @DeleteMapping
    public ResponseDTO<TodoDTO> deleteTodo(@AuthenticationPrincipal String userId, @RequestBody TodoDTO dto) {
        try {
            TodoEntity entity = TodoDTO.toEntity(dto);
            entity.setUserId(userId);

            List<TodoEntity> entities = service.delete(entity);
            List<TodoDTO> dtos = entities.stream()
                    .map(TodoDTO::new)
                    .collect(Collectors.toList());

            return ResponseDTO.<TodoDTO>builder().data(dtos).build();

        } catch (Exception e) {
            String error = e.getMessage();
            return ResponseDTO.<TodoDTO>builder().error(error).build();
        }
    }
}
