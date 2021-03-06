package kamillobinski.sheltybackend.service;

import kamillobinski.sheltybackend.entity.Animal;
import kamillobinski.sheltybackend.entity.Images;
import kamillobinski.sheltybackend.repository.AnimalRepository;
import kamillobinski.sheltybackend.repository.ImagesRepository;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import org.apache.commons.io.FilenameUtils;

@Service
public class ImagesService {

    @Autowired
    private AnimalService animalService;

    @Autowired
    private ImagesRepository imagesRepository;

    @Autowired
    private AnimalRepository animalRepository;

    public void saveImage(String animalId, MultipartFile image) {
        Animal animal = animalService.getAnimal(animalId);
        // Get and format current date to include in filename
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
        Date date = new Date();
        String fileName = animal.getName() + formatter.format(date) + "-gallery-image";
        // Get image extension
        String extension = "." + Objects.requireNonNull(image.getOriginalFilename()).substring(image.getOriginalFilename().lastIndexOf(".") + 1);

        try {
            byte[] bytes = image.getBytes();
            Path path = Paths.get(new File("src/main/webapp/WEB-INF/images/animal/gallery/" + fileName + extension).getAbsolutePath());
            Files.write(path, bytes);
            // Attach image to animal
            Images images = new Images();
            images.setAnimal(animal);
            images.setUrl(fileName + extension);
            imagesRepository.save(images);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    };

    public List<Images> getImages(String animalId) {
        Animal animal = animalService.getAnimal(animalId);
        return imagesRepository.findAllByAnimal(animal);
    }

    public Images getImage(String imageId) {
        int id = Integer.parseInt(imageId);
        return imagesRepository.findById(id);
    }

    public void deleteImage(String imageId) throws IOException {
        Images image = getImage(imageId);
        Path imagePath = Paths.get(new File("src/main/webapp/WEB-INF/images/animal/gallery/" + image.getUrl()).getAbsolutePath());
        imagesRepository.deleteById(Integer.parseInt(imageId));
        Files.delete(imagePath);
    }

    public void deleteImages(String animalId) {
        Animal animal = animalService.getAnimal(animalId);
        try {
            List<Images> images = imagesRepository.findAllByAnimal(animal);
            for (int i = 0; i <= images.size(); i++) {
                Path imagePath = Paths.get(new File("src/main/webapp/WEB-INF/images/animal/gallery/" + images.get(i).getUrl()).getAbsolutePath());
                Files.delete(imagePath);
            }
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        imagesRepository.deleteAllByAnimal(animal);
    }

    public void setAvatarFromImage(String imageId, String animalId) throws IOException {
        Animal animal = animalService.getAnimal(animalId);
        String previousAvatarName = animal.getAvatar();
        Images image = getImage(imageId);

        Path sourcePath = Paths.get(new File("src/main/webapp/WEB-INF/images/animal/gallery/" + image.getUrl()).getAbsolutePath());
        byte[] sourceBytes = Files.readAllBytes(sourcePath);
        String sourceExtension = FilenameUtils.getExtension(String.valueOf(sourcePath));

        // Get and format current date to include in filename
        SimpleDateFormat formatter = new SimpleDateFormat("ddMMyyyyHHmmss");
        Date date = new Date();
        String fileName = animal.getName() + formatter.format(date) + "-avatar";

        Path destPath = Paths.get(new File("src/main/webapp/WEB-INF/images/animal/avatar/" + fileName + "." + sourceExtension).getAbsolutePath());

        Files.write(destPath, sourceBytes);
        animal.setAvatar(fileName + "." + sourceExtension);
        animalRepository.save(animal);

        if (!previousAvatarName.equals("animal-avatar-default.jpg")) {
            Path previousPath = Paths.get(new File("src/main/webapp/WEB-INF/images/animal/avatar/" + previousAvatarName).getAbsolutePath());
            Files.delete(previousPath);
        }
    }
}
