package com.compass.ms_usuario.securities;

import com.compass.ms_usuario.models.User;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Getter
public class UserDetailsImpl implements UserDetails {

    private final User user;

    public UserDetailsImpl(User user) {
        this.user = user;
    }

    /**
     * Retorna as autoridades concedidas ao usuário.
     * Neste caso, não há autoridades específicas, então retorna uma lista vazia.
     *
     * @return uma coleção de autoridades concedidas
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    /**
     * Verifica se a conta não expirou.
     *
     * @return true se a conta não expirou, false caso contrário
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Verifica se a conta não está bloqueada.
     *
     * @return true se a conta não está bloqueada, false caso contrário
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Verifica se as credenciais do usuário não expiraram.
     *
     * @return true se as credenciais não expiraram, false caso contrário
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Verifica se o usuário está habilitado.
     *
     * @return true se o usuário está habilitado, false caso contrário
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}
