/* Developed by TechEveryWhere Engineering (C)2025 */
package com.plutospace.events.commons.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T, I> extends MongoRepository<T, I> {
}
