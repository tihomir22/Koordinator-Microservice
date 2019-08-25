package com.koordinator.epsilon.Koordinator.repositorio;

import com.koordinator.epsilon.Koordinator.entidades.AssetPrice;
import com.koordinator.epsilon.Koordinator.entidades.FormularioContactoWeb;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormularioContactoRepository extends MongoRepository<FormularioContactoWeb,String> {


}
