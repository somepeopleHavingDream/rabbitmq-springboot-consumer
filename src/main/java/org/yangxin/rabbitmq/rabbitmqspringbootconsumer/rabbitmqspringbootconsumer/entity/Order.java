package org.yangxin.rabbitmq.rabbitmqspringbootconsumer.rabbitmqspringbootconsumer.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author yangxin
 * 1/12/21 2:18 PM
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order implements Serializable {

    private String id;
    private String name;
}
