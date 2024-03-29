package unit;

import com.vision.project.controllers.RestaurantController;
import com.vision.project.models.*;
import com.vision.project.models.DTOs.MenuDto;
import com.vision.project.models.DTOs.RestaurantDto;
import com.vision.project.models.specs.RestaurantSpec;
import com.vision.project.services.RestaurantServiceImpl;
import com.vision.project.services.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RestaurantControllerTest {
    @InjectMocks
    private RestaurantController restaurantController;

    @Mock
    private UserServiceImpl userService;

    @Mock
    private RestaurantServiceImpl restaurantService;

    private final Restaurant restaurant = new Restaurant(1, "testName", "testAddress", "fast food", new ArrayList<>());
    private final UserModel userModel = new UserModel(1, "username", "email", "password", "ROLE_ADMIN", "firstName", "lastName", 25, "Bulgaria", restaurant);
    private final UserDetails user = new UserDetails(userModel, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    private final UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user, user.getId());

    @Test
    public void findById(){
        restaurant.setMenu(List.of(new Menu("menu", restaurant), new Menu("menu1", restaurant)));

        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userService.findById(user.getId())).thenReturn(userModel);
        when(restaurantService.findById(1, userModel)).thenReturn(restaurant);

        RestaurantDto restaurantDto = restaurantController.findById(1);
        List<MenuDto> menuDto = restaurantDto.getMenu();

        assertEquals(restaurantDto.getId(), restaurant.getId());
        assertEquals(restaurantDto.getAddress(), restaurant.getAddress());
        assertEquals(restaurantDto.getName(), restaurant.getName());
        assertEquals(restaurantDto.getType(), restaurant.getType());
        restaurant.getMenu().forEach(menu -> assertTrue(menuDto.contains(
                new MenuDto(menu.getName(), restaurant.getId()))));
    }

    @Test
    public void delete(){
        auth.setDetails(user);
        SecurityContextHolder.getContext().setAuthentication(auth);

        when(userService.findById(user.getId())).thenReturn(userModel);

        restaurantController.delete(1);

        verify(restaurantService, times(1)).delete(1, userModel);
    }

    @Test
    public void create(){
        ArgumentCaptor<Restaurant> captor = ArgumentCaptor.forClass(Restaurant.class);

        RestaurantSpec restaurantSpec = new RestaurantSpec("name", "type", "address",
                List.of("menu", "menu1"));
        Restaurant restaurant = new Restaurant(restaurantSpec);

        when(restaurantService.create(captor.capture())).thenReturn(restaurant);

        RestaurantDto restaurantDto = restaurantController.create(restaurantSpec);
        List<MenuDto> menuDto = restaurantDto.getMenu();
        Restaurant passedRestaurant = captor.getValue();

        assertEquals(restaurantDto.getId(), restaurant.getId());
        assertEquals(restaurantDto.getAddress(), restaurantSpec.getAddress());
        assertEquals(restaurantDto.getName(), restaurantSpec.getName());
        assertEquals(restaurantDto.getType(), restaurantSpec.getType());
        restaurantSpec.getMenu().forEach(menu -> assertTrue(menuDto.contains(
                new MenuDto(menu, restaurant.getId()))));

        assertEquals(passedRestaurant.getName(), restaurantSpec.getName());
        assertEquals(passedRestaurant.getType(), restaurantSpec.getType());
        assertEquals(passedRestaurant.getAddress(), restaurantSpec.getAddress());
    }
}
