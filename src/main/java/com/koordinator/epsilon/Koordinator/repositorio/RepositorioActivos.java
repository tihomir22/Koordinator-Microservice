package com.koordinator.epsilon.Koordinator.repositorio;

import com.koordinator.epsilon.Koordinator.entidades.PrecioActivo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RepositorioActivos extends MongoRepository<PrecioActivo,String> {


}
