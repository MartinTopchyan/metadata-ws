/*
 *
 * Copyright 2018 EMBL - European Bioinformatics Institute
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package uk.ac.ebi.ampt2d.metadata.persistence.repositories;

import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.data.querydsl.binding.QuerydslBinderCustomizer;
import org.springframework.data.querydsl.binding.QuerydslBindings;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import uk.ac.ebi.ampt2d.metadata.persistence.entities.Assembly;
import uk.ac.ebi.ampt2d.metadata.persistence.entities.QAssembly;

@RepositoryRestResource
public interface AssemblyRepository extends CrudRepository<Assembly, Long>,
        QueryDslPredicateExecutor<Assembly>, QuerydslBinderCustomizer<QAssembly> {

    default void customize(QuerydslBindings bindings, QAssembly assembly) {
        bindings.bind(assembly.name, assembly.patch)
                .first((path, value) -> path.equalsIgnoreCase(value));
        bindings.bind(assembly.accessions)
                .first((path, value) -> path.any().equalsIgnoreCase(value.iterator().next()));
    }

}
