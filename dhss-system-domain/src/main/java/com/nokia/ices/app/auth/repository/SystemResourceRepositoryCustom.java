package com.nokia.ices.app.auth.repository;

import java.util.List;

import com.nokia.ices.app.auth.domain.SystemResource;

public interface SystemResourceRepositoryCustom {

	List<SystemResource> findAllMenu(String token);

}
