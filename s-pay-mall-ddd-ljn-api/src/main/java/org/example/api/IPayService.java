package org.example.api;

import org.example.api.dto.CreatePayRequestDTO;
import org.example.api.response.Response;

import java.util.HashMap;

/**
 * @author dabuw
 * @description
 * @date 7/11/26
 */
public interface IPayService {

    Response<String> createPayOrder(CreatePayRequestDTO createPayRequestDTO);

}
