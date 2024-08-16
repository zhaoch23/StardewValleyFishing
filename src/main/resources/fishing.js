function options_methods() {
    var guiCacheMap = GuiScreen.getCacheMap();
    var dataMap = GuiOptions.getData();

    var Math = Utils.forClass("java.lang.Math");

    var Keyboard = Utils.forClass("org.lwjgl.input.Keyboard");
    var Mouse = Utils.forClass("org.lwjgl.input.Mouse");

    var GeometricRV = (p) => {
        let self = {
            "p": p,
            "threshold": p
        };

        self["yield"] = () => {
            if (Math.random() < self.threshold) {
                self.threshold = self.p;
                return 1; // Expected periods is  1/p
            } else {
                return 0;
            }
            
        };

        return self;
    };

    var UniformRV = (a, b) => {
        let self = {
            "a": a,
            "b": b
        };

        self["yield"] = () => {
            return self.a + Math.random() * (self.b - self.a);
        };

        return self;
    };

    var FishSSM = (fishing_bar_height, period_expected, peak_time, damping_ratio, distance_min, distance_max, behavior_pattern) => {
        
        let self = {
          "x_dot": [0.0, 0.0],
          "x": [fishing_bar_height, 0.0],
          "u": 0.0, // reference
          "omega_n": Math.PI / ((peak_time / 20) * Math.sqrt(1 - damping_ratio * damping_ratio)),
          "zeta": damping_ratio,
          "behavior_pattern": behavior_pattern,
          "behavior_index": 0,
          "dt": 0.05, // 20 Hz
          "fishing_bar_height": fishing_bar_height,
          "scale": fishing_bar_height / 100.0
        };

        self["periodRV"] = GeometricRV(1.0/period_expected);     // Geoemetric RV with expected period of period_expected
        self["distanceRV"] = UniformRV(distance_min, distance_max); // Uniform RV between distance_min and distance_max

        self.u = self.fishing_bar_height + self.distanceRV.yield() * behavior_pattern[self.behavior_index] * self.scale; // Initial reference
        
        self['next_behavior'] = () => {
            self.behavior_index = (self.behavior_index + 1) % size(self.behavior_pattern);
            let direction = self.behavior_pattern[self.behavior_index];
            if (direction == 0) {
                // Random behavior -1 or 1
                direction = (Math.random() < 0.5) ? -1 : 1;
            }
            let u = self.x[0] + self.distanceRV.yield() * direction * self.scale;
            return Math.min(Math.max(u, 0), self.fishing_bar_height);
        };

        self['step'] = () => {
            if (self.periodRV.yield() == 1) {
                self.u = self.next_behavior();
            }
            self.x_dot[0] = self.x[1];
            self.x_dot[1] = -1 * self.x[0] - 2 * self.zeta * self.omega_n * self.x[1] + self.u + self.omega_n * self.omega_n * (self.u - self.x[0]);

            self.x[0] = self.x[0] + self.x_dot[0] * self.dt;
            self.x[1] = self.x[1] + self.x_dot[1] * self.dt;

            let y = self.x[0];
            // Log.chat("x: {} y:{}, u: {}", self.x, y, self.u);
            return Math.min(Math.max(y, 0), self.fishing_bar_height);
        };
        
        return self;
    };

    var FishingRodModel = (fishing_bar_height, max_vel, acc, gravity) => {
        let self = {
            "fishing_bar_height": fishing_bar_height,
            "pos": fishing_bar_height,
            "vel": 0.0,
            "acc": acc,
            "gravity": gravity,
            "max_vel": max_vel
        };

        self["step"] = (is_pulling) => {
            let acc = is_pulling ? -1*self.gravity : self.acc;
            self.vel = Math.min(Math.max(self.vel + acc, -1 * self.max_vel), self.max_vel);
            self.pos = Math.min(Math.max(self.pos + self.vel, 0), self.fishing_bar_height);
            if (self.pos == self.fishing_bar_height || self.pos == 0) {
                // reset velocity
                self.vel = 0.0;
            }
            return self.pos;
        };

        return self;
    }

    var FishingService = () => {
        if (guiCacheMap.containsKey("fishingService")) {
            return guiCacheMap.get("fishingService");
        }
        
        let self = {
            "fish_widget": GuiScreen.getGuiPart("canvas").getGuiPart("canvas_fishing_bar").getGuiPart("fish"),
            "fish_widget_x": 0.0,
            "rod_widget": GuiScreen.getGuiPart("canvas").getGuiPart("canvas_fishing_bar").getGuiPart("rod"),
            "progress_bar_widget": GuiScreen.getGuiPart("canvas").getGuiPart("progress_bar"),
            "uniformRV": UniformRV(-1, 1),   
            "progress": 0,
            "progress_bar_height": 0.0,
            "state": 0,
            "initialized": false,
            "fishing_bar_height": 0.0
        };

        self['preinit'] = () => {
            self.fishing_bar_height = dataMap.get("fishing_bar_height");
            self.progress_bar_height = self.fishing_bar_height + dataMap["progress_bar_offset"];
            GuiScreen.getGuiPart("canvas").getGuiPart("progress_bar").setLocationY(self.progress_bar_height);
            self.initialized = self.fish_state != null;
        };
        
        self['init'] = (fishDTO, rodDTO) => {  
    
            self["fish_state"] = FishSSM(self.fishing_bar_height, fishDTO["period"], fishDTO["peak_time"], fishDTO["damping_ratio"], fishDTO["distance_min"], fishDTO["distance_max"], fishDTO["behavior_pattern"]);
            self.fish_widget_x = self.fish_widget.getLocationX();
            self.fish_widget.setLocationY(self.fishing_bar_height);
            
            let rod_fishing_bar_height = Math.min(self.fishing_bar_height, self.fishing_bar_height - (rodDTO['scale'] - 1) * self.rod_widget.getHeight());
            self["rod_state"] = FishingRodModel(rod_fishing_bar_height, rodDTO["max_vel"], rodDTO["acc"], rodDTO["gravity"]);
            self.rod_widget.setHeight(rodDTO['scale'] * self.rod_widget.getHeight());
            self.progress = rodDTO["initial_progress"];

            self.initialized = self.fishing_bar_height != 0;
        };

        self['step'] = () => {
            // Space or mouse click
            let is_pulling = Keyboard.isKeyDown(Keyboard.KEY_SPACE ) || Mouse.isButtonDown(0);

            // poll keyboard
            Keyboard.poll();
            

            let fish_pos = self.fish_state.step();
            let rod_pos = self.rod_state.step(is_pulling);

            // Check if fish is hooked (AABB collision)
            if ((fish_pos + self.fish_widget.getHeight() > rod_pos && fish_pos < rod_pos + self.rod_widget.getHeight()) ||
                (rod_pos + self.rod_widget.getHeight() > fish_pos && rod_pos < fish_pos + self.fish_widget.getHeight())) {
                // Fish is hooked increase progress
                self.progress += 1;
                    
                // Shake the fish
                Log.chat("hooked");
                self.fish_widget.setLocationY(fish_pos + self.uniformRV.yield() * 0.5);
                self.fish_widget.setLocationX(self.uniformRV.yield() * 0.5);

                if (self.progress >= 100) {
                    // Fish is caught TODO: call server
                    Log.chat("fish caught");
                    self.state = 1;
                    GuiScreen.post("caughtFish", {
                        "perfect": false // TODO
                    });
                }
            } else {
                // Fish is not hooked
                self.progress = Math.max(self.progress - 1, 0);

                self.fish_widget.setLocationY(fish_pos);
                self.fish_widget.setLocationX(0);

                if (self.progress == 0) {
                    // Fish is lost TODO: call server
                    Log.chat("fish lost");
                    self.state = 2;
                    GuiScreen.post("attemptFailed", {
                        "dummy_frame": 0
                    });
                }
            }

            self.progress_bar_widget.setHeight(self.progress * self.progress_bar_height / 100);
            self.progress_bar_widget.setLocationY(self.progress_bar_height - self.progress_bar_widget.getHeight());
            //self.fish_widget.setLocationY(fish_pos);
            self.rod_widget.setLocationY(rod_pos);
        };
        
        guiCacheMap.put("fishingService", self);
        return self;
    };  
}


function options_tickScript() {
    if (FishingService().initialized && FishingService().state == 0) {
        FishingService().step();
    }
}

function options_dataScript() {
    Log.chat("{}", dataMap);
    if (dataMap.containsKey("fishing_bar_height")) {
        FishingService().preinit();
        FishingService().init(
           {'period': 100, 'peak_time': 40, 'damping_ratio': 0.9, 'distance_min': 20.0, 'distance_max': 40.0, 'behavior_pattern': [-1, -1, 0]}, 
           {'max_vel': 10.0, 'acc': 0.5, 'gravity': 0.5, 'scale': 2.0, 'initial_progress': 30}
        );
    } else if (dataMap.containsKey("period_expected")) {
        //TODO
    }
}