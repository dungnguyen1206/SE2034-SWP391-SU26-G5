package vn.edu.fpt.SE2034_SWP391_G5.config;

import com.cloudinary.Cloudinary;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class CloudinaryConfig {


    @Value("${cloudinary.cloud-name}")
    private  String cloudinaryName;

    @Value("${cloudinary.api-key}")
    private  String apiKey;

    @Value("${cloudinary.api-secret}")
    private  String apiSecret;

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name",cloudinaryName);
        config.put("api_key",apiKey);
        config.put("api_secret",apiSecret);
        return new Cloudinary(config);
    }

}
