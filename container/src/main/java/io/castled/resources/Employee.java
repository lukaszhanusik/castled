package io.castled.resources;

import io.castled.resources.models.Address;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Employee {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private Address address;
}
