package hu.bnorbi.costtracker.converter;

import hu.bnorbi.costtracker.entity.User;
import org.springframework.core.convert.converter.Converter;

public class StringToUserConverter implements Converter<String, User> {

    @Override
    public User convert(String from) {
        String[] data = from.split(",");
        return new User(
                Long.parseLong(data[0]),
                data[1],
                data[2],
                data[3],
                data[4],
                data[5]
        );
    }
}
