
package com.nhnacademy.bookapi.service.wrappable;


import com.nhnacademy.bookapi.repository.BookRepository;
import com.nhnacademy.bookapi.repository.WrapperRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class WrapperService {

    private final WrapperRepository wrapperRepository;
    private final BookRepository bookRepository;


}
