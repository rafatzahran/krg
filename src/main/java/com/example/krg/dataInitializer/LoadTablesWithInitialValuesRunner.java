package com.example.krg.dataInitializer;

import com.example.krg.models.ERole;
import com.example.krg.models.EUnit;
import com.example.krg.models.Role;
import com.example.krg.models.Unit;
import com.example.krg.models.User;
import com.example.krg.models.UserRole;
import com.example.krg.repository.RoleRepository;
import com.example.krg.repository.UnitRepository;
import com.example.krg.repository.UserRepository;
import com.example.krg.repository.UserRoleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

@Component
public class LoadTablesWithInitialValuesRunner implements ApplicationRunner {
    private static final Logger log = LoggerFactory.getLogger(LoadTablesWithInitialValuesRunner.class);

    private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Autowired
    UserRepository repoUser;

    @Autowired
    RoleRepository repoRole;

    @Autowired
    UnitRepository repoUnit;

    @Autowired
    UserRoleRepository repoUserRole;

    @Override
    public void run(ApplicationArguments args) {


        List<User> users = Arrays.asList(
                new User(1L, "Alice", 1L),
                new User(2L, "Bob", 2L),
                new User(3L, "Eve", 1L)
        );
        try {
            repoUser.saveAll(users);
        } catch (Exception e) {
            log.warn("Could not load initial values to user table. e={}", e.getMessage());
        }

        List<Unit> units = Arrays.asList(
                new Unit(11L, EUnit.KREFTREGISTERET, 2L),
                new Unit(12L, EUnit.AKERSHUS_HF, 1L),
                new Unit(13L, EUnit.SOUTH_HF, 2L),
                new Unit(14L, EUnit.WEST_HF, 2L)
        );
        try {
            repoUnit.saveAll(units);
        } catch (Exception e) {
            log.warn("Could not load initial values to unit table. e={}", e.getMessage());
        }

        List<Role> roles = Arrays.asList(
                new Role(ERole.USER_ADMINISTRATION, 1L),
                new Role(ERole.ENDOSCOPIST_ADMINISTRATION, 2L),
                new Role(ERole.REPORT_COLONOSCOPY_CAPACITY, 1L),
                new Role( ERole.SEND_INVITAIONS, 2L),
                new Role(ERole.VIEW_STATISTICS, 1L)
        );
        try {
            repoRole.saveAll(roles);
        } catch (Exception e) {
            log.warn("Could not load initial values to role table. e={}", e.getMessage());
        }


        List<UserRole> userRoleList = Arrays.asList(
                new UserRole(1001L, 1L, 1L, 11L, 101L,
                        LocalDateTime.parse("2019-01-02 00:00:00", formatter),
                        LocalDateTime.parse("2019-12-31 23:59:59", formatter)),
                new UserRole(1002L, 2L, 1L, 11L, 104L,
                        LocalDateTime.parse("2019-01-02 00:00:00", formatter),
                        LocalDateTime.parse("2019-12-31 23:59:59", formatter)),
                new UserRole(1003L, 1L, 1L, 11L, 105L,
                        LocalDateTime.parse("2019-06-11 00:00:00", formatter),
                        LocalDateTime.parse("2019-12-31 23:59:59", formatter)),
                new UserRole(1004L, 2L, 2L, 12L, 101L,
                        LocalDateTime.parse("2020-01-28 00:00:00", formatter), null),
                new UserRole(1005L, 1L, 2L, 12L, 105L,
                        LocalDateTime.parse("2020-01-28 00:00:00", formatter), null),
                new UserRole(1006L, 1L, 2L, 14L, 101L,
                        LocalDateTime.parse("2020-01-28 00:00:00", formatter), null),
                new UserRole(1007L, 1L, 2L, 14L, 102L,
                        LocalDateTime.parse("2020-01-28 00:00:00", formatter), null),
                new UserRole(1008L, 1L, 1L, 11L, 101L,
                        LocalDateTime.parse("2020-02-01 07:00:00", formatter), null),
                new UserRole(1009L, 1L, 1L, 11L, 104L,
                        LocalDateTime.parse("2020-02-01 07:00:00", formatter), null)
        );

        try {
            for (UserRole userRole : userRoleList) {
                repoUserRole.insertUserRoleWithQuery(userRole);
            }
        } catch (Exception e) {
            log.warn("Could not load initial values to userRole table. e={}", e.getMessage());
        }
    }
}