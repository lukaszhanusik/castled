package io.castled.resources.models;

import io.castled.resources.Employee;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BulkEmployeeDTO {

    private List<Employee> employees;
}
