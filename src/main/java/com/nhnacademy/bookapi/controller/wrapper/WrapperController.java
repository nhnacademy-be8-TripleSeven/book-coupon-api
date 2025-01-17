package com.nhnacademy.bookapi.controller.wrapper;

import com.nhnacademy.bookapi.service.wrappable.WrapperService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class WrapperController {


    private final WrapperService wrapperService;
}
