package hello;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

@SpringBootApplication
@RestController
public class Application {

    static class Self {
        public String href;
    }

    static class Links {
        public Self self;
    }

    static class PlayerState {
        public Integer x;
        public Integer y;
        public String direction;
        public Boolean wasHit;
        public Integer score;
    }

    static class Arena {
        public List<Integer> dims;
        public Map<String, PlayerState> state;
    }

    static class ArenaUpdate {
        public Links _links;
        public Arena arena;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.initDirectFieldAccess();
    }

    @GetMapping("/")
    public String index() {
        return "Let the battle begin!";
    }

    @PostMapping("/**")
    public String index(@RequestBody ArenaUpdate arenaUpdate) {
        System.out.println(arenaUpdate._links.self.href);
        Integer arenaX = arenaUpdate.arena.dims.get(0);
        Integer arenaY = arenaUpdate.arena.dims.get(1);

        String[][] arena = new String[arenaX][arenaY];

        PlayerState me = new PlayerState();

        for (Map.Entry<String, PlayerState> entry : arenaUpdate.arena.state.entrySet()) {
            if (entry.getKey().equals(arenaUpdate._links.self.href)) {
                me = entry.getValue();
            }

            PlayerState ps = entry.getValue();
            arena[ps.x][ps.y] = entry.getKey();
        }

        boolean facingX = true;
        Integer fromX = 0;
        Integer fromY = 0;
        Integer toX = 0;
        Integer toY = 0;

        switch (me.direction) {
            case "N":
                facingX = false;
                fromY = me.y;
                toY = me.y + 2;
                fromX = me.x;
                toX = me.x;
                if (toY > arenaY) {
                    toY = arenaY;
                }
                break;
            case "S":
                fromY = me.y - 2;
                toY = me.y;
                fromX = me.x;
                toX = me.x;
                if (fromY < 0) {
                    fromY = 0;
                }
                break;
            case "E":
                fromY = me.y;
                toY = me.y;
                fromX = me.x;
                toX = me.x + 2;
                if (fromX > arenaX) {
                    fromX = arenaX;
                }
                facingX = true;
                break;
            case "W":
                fromY = me.y;
                toY = me.y;
                fromX = me.x - 2;
                toX = me.x;
                if (fromX < 0) {
                    fromX = 0;
                }
                facingX = true;
                break;
        }
        boolean someoneExists = false;
        for (int i = fromX; i <= toX; i++) {
            for (int j = fromY; j <= toY; j++) {
                if (arena[i][j] != null && !arena[i][j].equals(arenaUpdate._links.self.href)) {
                    someoneExists = true;
                }
            }
        }

        String[] commands = new String[]{"F", "R", "L", "T"};
        int i = new Random().nextInt(4);
        if (someoneExists) {
            return "T";
        }
        return commands[i];
    }

}

