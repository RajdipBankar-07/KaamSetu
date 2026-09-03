package com.kaamsetu.core.security;

import com.kaamsetu.modules.provider.repository.ProviderRepository;
import com.kaamsetu.modules.user.entity.UserEntity;
import com.kaamsetu.modules.user.repository.UserRepository;
import com.kaamsetu.modules.worker.repository.WorkerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final WorkerRepository workerRepository;
    private final ProviderRepository providerRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrMobile) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsernameIgnoreCase(usernameOrMobile)
                .or(() -> userRepository.findByMobile(usernameOrMobile))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username or mobile: " + usernameOrMobile));

        boolean hasWorker = workerRepository.findByUserId(user.getId()).isPresent();
        boolean hasProvider = providerRepository.findByUserId(user.getId()).isPresent();
        return UserPrincipal.create(user, hasWorker, hasProvider);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(UUID id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + id));

        boolean hasWorker = workerRepository.findByUserId(user.getId()).isPresent();
        boolean hasProvider = providerRepository.findByUserId(user.getId()).isPresent();
        return UserPrincipal.create(user, hasWorker, hasProvider);
    }
}
