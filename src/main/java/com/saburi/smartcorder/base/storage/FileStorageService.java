package com.saburi.smartcorder.base.storage;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.saburi.smartcorder.base.Model;
import com.saburi.smartcorder.base.exceptions.KnownException;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileStorageService<T extends Model, I> implements StorageService<T, I> {

    private final Path rootLocation;
    private final ObjectMapper objectMapper;
    private final Class<T> entityClass;

    protected String getFileName() {
        return entityClass.getSimpleName().concat(".json");
    }



    protected Path getFileDestination() {
        return this.rootLocation.resolve(
                Paths.get(getFileName())
                        .normalize().toAbsolutePath());
    }



    public FileStorageService(StorageProperties properties, ObjectMapper objectMapper) {
        this.rootLocation = Paths.get(properties.getLocation());
        this.objectMapper = objectMapper;
        this.entityClass = (Class<T>) ((ParameterizedType) getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        try {
            init();
        } catch (KnownException e) {
            throw new RuntimeException(e);
        }
    }

    private void init() throws KnownException {
        try {
            Files.createDirectories(rootLocation);
        } catch (IOException e) {
            throw new KnownException("Could not initialize storage");
        }
    }

    @Override
    public T save(T t) throws KnownException {
        try {
            Path destinationFile = getFileDestination();
            List<T> existingEntities = findAll();

            // Check if entity already exists
            Optional<T> existing = existingEntities.stream()
                    .filter(e -> e.getId().equals(t.getId()))
                    .findFirst();

            if (existing.isPresent()) {
                // Update existing
                existingEntities.remove(existing.get());
            }
            existingEntities.add(t);

            String json = objectMapper.writeValueAsString(existingEntities);
            Files.write(destinationFile, json.getBytes());
            return t;
        } catch (IOException e) {
            throw new KnownException("Failed to store data: " + e.getMessage());
        }
    }



    @Override
    public List<T> saveAll(List<T> entities) throws KnownException {
        if (entities.isEmpty()) {
            return entities;
        }

        try {
            Path destinationFile = getFileDestination();
            List<T> existingEntities = findAll();

            // Remove any existing entities that are being updated
            entities.forEach(newEntity -> {
                existingEntities.removeIf(e -> e.getId().equals(newEntity.getId()));
            });

            // Add all new/updated entities
            existingEntities.addAll(entities);

            String json = objectMapper.writeValueAsString(existingEntities);
            Files.write(destinationFile, json.getBytes());
            return entities;
        } catch (IOException e) {
            throw new KnownException("Failed to store multiple entities: " + e.getMessage());
        }
    }




    @Override
    public Optional<T> findById(I id) {
        try {
            List<T> entities = findAll();
            return entities.stream()
                    .filter(e -> e.getId().equals(id))
                    .findFirst();
        } catch (Exception e) {
            throw new RuntimeException("Failed to find entity by id: " + id, e);
        }
    }

    @Override
    public List<T> findAll() {
        try {
            Path file = getFileDestination();
            if (!Files.exists(file)) {
                return new ArrayList<>();
            }

            String json = new String(Files.readAllBytes(file));
            return objectMapper.readValue(json, new TypeReference<List<T>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to retrieve all entities of type " + this.entityClass.getSimpleName(), e);
        }
    }



    @Override
    public boolean deleteById(I id) {
        try {
            Path file = getFileDestination();
            List<T> entities = findAll();

            boolean removed = entities.removeIf(e -> e.getId().equals(id));
            if (!removed) {
                return false;
            }

            String json = objectMapper.writeValueAsString(entities);
            Files.write(file, json.getBytes());
            return true;
        } catch (Exception e) {
            throw new RuntimeException("Failed to delete entity with id: " + id, e);
        }
    }

    @Override
    public void deleteAll() {
        try {
            Path file = getFileDestination();
            if (Files.exists(file)) {
                Files.delete(file);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete all entities of type " + this.entityClass.getSimpleName(), e);
        }
    }

    @Override
    public boolean existsById(I id) {
        return findById(id).isPresent();
    }

    @Override
    public long count() {
        return findAll().size();
    }
}
