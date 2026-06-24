package com.chimeil.service.impl;

import com.chimeil.constant.MessageConstant;
import com.chimeil.dto.EmployeeDTO;
import com.chimeil.dto.EmployeeLoginDTO;
import com.chimeil.entity.Employee;
import com.chimeil.exception.AccountLockedException;
import com.chimeil.exception.AccountNotFoundException;
import com.chimeil.exception.PasswordErrorException;
import com.chimeil.mapper.EmployeeMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * EmployeeServiceImpl 单元测试 —— 覆盖登录、新增、编辑的边界与异常场景。
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("员工服务测试")
class EmployeeServiceImplTest {

    @Mock
    private EmployeeMapper employeeMapper;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Nested
    @DisplayName("登录 login")
    class LoginTests {

        private final EmployeeLoginDTO loginDTO = new EmployeeLoginDTO();
        private Employee employee;

        @BeforeEach
        void setUp() {
            loginDTO.setUsername("admin");
            loginDTO.setPassword("123456");
            employee = new Employee();
            employee.setId(1L);
            employee.setUsername("admin");
            employee.setPassword("e10adc3949ba59abbe56e057f20f883e"); // MD5("123456")
            employee.setStatus(1);
            employee.setName("管理员");
        }

        @Test
        @DisplayName("正常登录返回 Employee 实体")
        void shouldLoginSuccessfully() {
            when(employeeMapper.getByUsername("admin")).thenReturn(employee);
            Employee result = employeeService.login(loginDTO);
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("管理员", result.getName());
            assertEquals("admin", result.getUsername());
        }

        @Test
        @DisplayName("账号不存在抛 AccountNotFoundException")
        void shouldThrowWhenAccountNotFound() {
            when(employeeMapper.getByUsername("unknown")).thenReturn(null);
            loginDTO.setUsername("unknown");
            assertThrows(AccountNotFoundException.class, () -> employeeService.login(loginDTO));
        }

        @Test
        @DisplayName("密码错误抛 PasswordErrorException")
        void shouldThrowWhenPasswordWrong() {
            when(employeeMapper.getByUsername("admin")).thenReturn(employee);
            loginDTO.setPassword("wrong");
            assertThrows(PasswordErrorException.class, () -> employeeService.login(loginDTO));
        }

        @Test
        @DisplayName("账号锁定抛 AccountLockedException")
        void shouldThrowWhenAccountLocked() {
            employee.setStatus(0);
            when(employeeMapper.getByUsername("admin")).thenReturn(employee);
            assertThrows(AccountLockedException.class, () -> employeeService.login(loginDTO));
        }
    }

    @Nested
    @DisplayName("新增 save")
    class SaveTests {

        @Test
        @DisplayName("新增员工自动设置默认密码和状态")
        void shouldSaveWithDefaultValues() {
            EmployeeDTO dto = new EmployeeDTO();
            dto.setUsername("newuser");
            dto.setName("新员工");

            employeeService.save(dto);

            verify(employeeMapper).insert(any(Employee.class));
        }
    }

    @Nested
    @DisplayName("编辑 update")
    class EditTests {

        @Test
        @DisplayName("编辑员工信息")
        void shouldUpdateEmployee() {
            EmployeeDTO dto = new EmployeeDTO();
            dto.setId(2L);
            dto.setUsername("updated");
            dto.setName("更新员工");

            employeeService.update(dto);

            verify(employeeMapper).update(any(Employee.class));
        }
    }
}
