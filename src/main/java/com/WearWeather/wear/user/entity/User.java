package com.WearWeather.wear.user.entity;

import com.WearWeather.wear.auth.entity.Authority;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "`user`")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

   @Id
   @Column(name = "user_id")
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private long userId;

   @Column(name = "email", nullable = false)
   private String email;

   @Column(name = "password", length = 100, nullable = false)
   private String password;

   @Column(name = "name", length = 50, nullable = false)
   private String name;

   @Column(name = "nickname", length = 50, nullable = false)
   private String nickname;

   @Column(name = "is_social", nullable = false)
   private boolean isSocial;

   @Column(name = "activated")
   private boolean activated;

   @ManyToMany
   @JoinTable(
       name = "user_authority",
       joinColumns = {@JoinColumn(name = "user_id", referencedColumnName = "user_id")},
       inverseJoinColumns = {@JoinColumn(name = "authority_name", referencedColumnName = "authority_name")})
   private Set<Authority> authorities;
}
