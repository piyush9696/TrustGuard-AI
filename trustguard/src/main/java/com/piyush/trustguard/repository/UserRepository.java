package com.piyush.trustguard.repository;

import com.piyush.trustguard.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long>
{

}
