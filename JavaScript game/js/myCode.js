let paused = false;     // `paused` stops all update/draw logic when true
let audioStarted = false;   // `audioStarted` ensures background music only kicks off once   

// listen for key presses to control pause/resume & start audio
window.addEventListener('keydown', e => {
    // on first key press, start background music
    if (!audioStarted) {
        bgMusic.play();     // play the looping background track
        audioStarted = true;    // flag that we've started audio
    }
    // Press 'P' to pause game logic and music
    if (e.key === 'p') {
        paused = true;
        bgMusic.pause();
    }
    // Press 'C' to continue game logic and resume music
    if (e.key === 'c') {
        paused = false;
        bgMusic.play();
    }
});

// each array defines a set of background layers, with a src image and scroll speed
// a speed of 0 means the layer is static; higher speeds move faster for depth effect
const layerSourcesForest = [
    { src: 'images/forest/sky.png', speed: 0 },
    { src: 'images/forest/mountain.png', speed: 0.3 },
    { src: 'images/forest/river.png', speed: 0.1 },
    { src: 'images/forest/trees.png', speed: 0.5 },
];

const layerSourcesCave = [
    { src: 'images/cave/1.png', speed: 0 },
    { src: 'images/cave/2.png', speed: 0.1 },
    { src: 'images/cave/3.png', speed: 0.3 },
    { src: 'images/cave/4.png', speed: 0.5 },
];

const layerSourcesCastle = [
    { src: 'images/castle/1.png', speed: 0 },
    { src: 'images/castle/2.png', speed: 0.1 },
    { src: 'images/castle/3.png', speed: 0.2 },
    { src: 'images/castle/4.png', speed: 0.3 },
    { src: 'images/castle/5.png', speed: 0.4 },
    { src: 'images/castle/6.png', speed: 0.5 },
    { src: 'images/castle/7.png', speed: 0.6 },
];

let prince, lives;
const maxLives = 5;     // max hearts the player can have
const canvas = document.getElementById('canvas');
const GAME_WIDTH = canvas.width;    // canvas dimensions
const GAME_HEIGHT = canvas.height;
let cameraX = 0;     // horizontal scroll offset of the world
const LEVEL_WIDTH = GAME_WIDTH * 3;     // each level spans 3 screen-widths

class BackgroundSprite extends Sprite {
    constructor(image, speed) {
        super();
        this.image = image;
        this.speed = speed;
        this.x = 0;
    }

    update() {
        if (paused) return;
    }

    // draws the layer tiled 3 times to cover scrolling
    draw(ctx) {
        const offset = -cameraX * this.speed;
        for (let i = 0; i < 3; i++) {
            ctx.drawImage(
                this.image,
                offset + i * GAME_WIDTH,
                0,
                GAME_WIDTH,
                GAME_HEIGHT
            );
        }
    }
}

class Lives extends Sprite {
    constructor(maxLives, imagePath, size = 32) {
        super();
        this.maxLives = maxLives;
        this.lives = maxLives;      // start full health
        this.size = size;
        this.padding = 10;      // spacing between hearts
        this.heartImage = new Image();
        this.heartImage.src = imagePath;    // preload heart icon
    }

    update() {
        if (paused) return;
    }

    draw(ctx) {
        for (let i = 0; i < this.lives; i++) {
            // calculate position: padding + index*(heart size + padding)
            const x = this.padding + i * (this.size + this.padding);
            const y = this.padding;

            ctx.drawImage(
                this.heartImage,
                0, 0,
                this.heartImage.width,
                this.heartImage.height,
                x, y,
                this.size, this.size
            );
        }
    }
}

// encapsulates player movement, physics, animation state, and input handling
class Prince extends Sprite {
    constructor() {
        super();
        this.x = 0;
        this.y = 395;
        this.scale = 2;
        this.frame = 0;
        this.counter = 0;
        this.dx = 0;
        this.vy = 0;
        this.onGround = true;
        this.gravity = 0.8;
        this.jumpStrength = -20;

        // animation definitions: each contains an Image and frame info
        this.animations = {
            idle: {
                image: this.loadImage('images/prince/idle.png'),
                frameCount: 7,
                frameWidth: 96,
                frameHeight: 84
            },
            walkRight: {
                image: this.loadImage('images/prince/walk right.png'),
                frameCount: 8,
                frameWidth: 96,
                frameHeight: 84
            },
            walkLeft: {
                image: this.loadImage('images/prince/walk left.png'),
                frameCount: 8,
                frameWidth: 96,
                frameHeight: 84
            },
            attackRight: {
                image: this.loadImage('images/prince/attack right.png'),
                frameCount: 6,
                frameWidth: 96,
                frameHeight: 84
            },
            attackLeft: {
                image: this.loadImage('images/prince/attack left.png'),
                frameCount: 6,
                frameWidth: 96,
                frameHeight: 84
            }
        };

        // start in idle animation by default
        this.currentAnimation = 'idle';
    }

    // helper to load an image and assign its src
    loadImage(src) {
        const img = new Image();
        img.src = src;
        return img;
    }

    update(sprites, keys) {
        // skip all movement/animation when paused
        if (paused) return;

        // check for death condition: no lives left
        if (lives.lives <= 0) {
            loss.play();
            const lvl = game.levels[game.currentLevelIndex];
            game.changeLevel(lvl.loseLevel);
            return;
        }

        const worldX = cameraX + this.x;

        // check for win condition: reach end of level
        if (worldX >= LEVEL_WIDTH - 50) {
            victory.play();
            const lvl = game.levels[game.currentLevelIndex];
            game.changeLevel(lvl.winLevel);
            return;
        }

        // determine movement speed: faster in air
        const baseSpeed = 3;
        const airMultiplier = 2;
        const speed = this.onGround ? baseSpeed : baseSpeed * airMultiplier;

        const prevX = this.x;

        // handle horizontal input, with camera scrolling logic
        if (keys["ArrowRight"]) {
            if (cameraX < LEVEL_WIDTH - GAME_WIDTH && this.x >= GAME_WIDTH / 2) {
                cameraX = Math.min(cameraX + speed, LEVEL_WIDTH - GAME_WIDTH);
                this.dx = speed;
            } else {
                this.x = Math.min(this.x + speed, GAME_WIDTH);
                this.dx = speed;
            }
            this.currentAnimation = 'walkRight';

        } else if (keys["ArrowLeft"]) {
            if (cameraX > 0 && this.x <= GAME_WIDTH / 2) {
                cameraX = Math.max(cameraX - speed, 0);
                this.dx = -speed;
            } else {
                this.x = Math.max(this.x - speed, 0);
                this.dx = -speed;
            }
            this.currentAnimation = 'walkLeft';

        } else if (keys["s"]) {
            this.dx = 0;
            this.currentAnimation = 'attackRight';
        } else if (keys["a"]) {
            this.dx = 0;
            this.currentAnimation = 'attackLeft';

        } else {
            this.dx = 0;
            this.currentAnimation = 'idle';
        }


        this.dx = this.x - prevX;

        // jump logic: only allow jump when onGround
        if ((keys[" "] || keys["Space"]) && this.onGround) {
            this.vy = this.jumpStrength;
            this.onGround = false;
        }

        // gravity
        this.vy += this.gravity;
        this.y += this.vy;
        if (this.y >= 395) {
            this.y = 395;
            this.vy = 0;
            this.onGround = true;
        }

        this.counter++;
        if (this.counter >= 6) {
            const anim = this.animations[this.currentAnimation];
            this.frame = (this.frame + 1) % anim.frameCount;
            this.counter = 0;
        }
    }

    draw(ctx) {
        const anim = this.animations[this.currentAnimation];

        ctx.drawImage(
            anim.image,
            this.frame * anim.frameWidth, 0,
            anim.frameWidth, anim.frameHeight,
            this.x, this.y,
            200, 200
        );
    }

}

class Princess extends Sprite {
    constructor(x, y, initialState = "inLevel") {
        super();
        this.x = x;
        this.y = y;
        this.frame = 0;
        this.counter = 0;

        this.animations = {
            inLevel: {
                image: this.loadImage("images/princess/inLevel.png"),
                frameCount: 3,
                frameWidth: 34,
                frameHeight: 52,
            },
            lose: {
                image: this.loadImage("images/princess/lose.png"),
                frameCount: 5,
                frameWidth: 33,
                frameHeight: 53,
            },
            win: {
                image: this.loadImage("images/princess/win.png"),
                frameCount: 9,
                frameWidth: 42,
                frameHeight: 80,
            },
        };
        this.currentState = initialState;
    }

    loadImage(src) {
        const img = new Image();
        img.src = src;
        return img;
    }

    // switches the clip to one of 'inLevel', 'win', or 'lose' 
    setState(state) {
        if (this.animations[state]) {
            this.currentState = state;
            this.frame = 0;     // reset frame index when changing state
            this.counter = 0;   // reset counter
        }
    }

    update(sprites, keys, mouse) {
        this.counter++;
        if (this.counter >= 7) {
            this.counter = 0;
            const anim = this.animations[this.currentState];
            this.frame = (this.frame + 1) % anim.frameCount;
        }
    }

    // draw offset by cameraX only when in-level (static screen when winning/losing)
    draw(ctx) {
        const anim = this.animations[this.currentState];
        const screenX = this.currentState === "inLevel" ? this.x - cameraX : this.x;    // follow camera or fixsed center

        const visibleFrameWidth = 31;

        ctx.drawImage(
            anim.image,
            this.frame * anim.frameWidth,
            0,
            visibleFrameWidth,
            anim.frameHeight,
            screenX,
            this.y,
            visibleFrameWidth * 2,
            anim.frameHeight * 2
        );
    }
}

// represents a walking skeleton that damages the prince on collision unless attacking
class Skeleton extends Sprite {
    constructor(imagePath, x, y, width, height) {
        super();
        var skeletonSpritesheet = new Image();
        skeletonSpritesheet.src = imagePath;
        this.spritesheet = skeletonSpritesheet;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.numberOfFrames = 7;
        this.frameIndex = 0;
        this.counter = 0;
        this.speed = 2;
        this.hasHitPrince = false;      // to prevent multiple life deductions of prince per overlap 
        this.alive = true;      // alive state toggles off when killed by prince
    }

    update(sprites, keys) {
        if (paused) return;      // don't animate or move if paused 

        // don't animate or move if dead
        if (!this.alive) {
            return;
        }

        this.counter++;
        if (this.counter >= 6) {
            this.counter = 0;
            this.frameIndex++;
            if (this.frameIndex >= this.numberOfFrames) {
                this.frameIndex = 0;
            }
        }

        // move left across the screen
        this.x -= this.speed;

        // calculate collision box for this skeleton
        const frameW = this.width / this.numberOfFrames;
        const drawW = frameW * 1.5;
        const drawH = this.height * 2;
        const sLeft = this.x - cameraX + 110;
        const sTop = this.y;
        const sRight = sLeft + drawW - 100;
        const sBottom = sTop + drawH;

        // calculate prince collision box
        const pLeft = prince.x + 55;
        const pTop = prince.y + 50;
        const pRight = pLeft + 80;
        const pBottom = pTop + 150;

        // check overlap
        const overlapping =
            pLeft < sRight &&
            pRight > sLeft &&
            pTop < sBottom &&
            pBottom > sTop;

        if (overlapping) {
            if (!this.hasHitPrince) {
                // if prince is attacking, skeleton dies
                if (prince.currentAnimation === 'attackLeft' || prince.currentAnimation === 'attackRight') {
                    this.alive = false;
                    return;
                } else {
                    // otherwise, prince takes damage once per contact
                    lives.lives = Math.max(0, lives.lives - 1);
                    this.hasHitPrince = true;
                }
            }
        } else {
            // reset hit flag once no longer overlapping
            this.hasHitPrince = false;
        }
    }

    // draw current frame of skeleton if alive 
    draw(ctx) {
        if (!this.alive) return;
        const frameW = this.width / this.numberOfFrames;

        ctx.drawImage(
            this.spritesheet,
            this.frameIndex * frameW, 0,
            frameW, this.height,
            this.x - cameraX, this.y,
            frameW * 2, this.height * 2
        );
    }
}

// similar to `Skeleton` class
class Hyena extends Sprite {
    constructor(imagePath, x, y, width, height) {
        super();
        var hyenaSpritesheet = new Image();
        hyenaSpritesheet.src = imagePath;
        this.spritesheet = hyenaSpritesheet;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.numberOfFrames = 6;
        this.frameIndex = 0;
        this.counter = 0;
        this.speed = 2;
        this.hasHitPrince = false;
        this.alive = true;
    }

    update() {
        if (paused) return;

        if (!this.alive) {
            return;
        }

        this.counter++;
        if (this.counter >= 5) {
            this.counter = 0;
            this.frameIndex++;
            if (this.frameIndex >= this.numberOfFrames) {
                this.frameIndex = 0;
            }
        }

        this.x -= this.speed;

        const frameW = this.width / this.numberOfFrames;
        const drawW = frameW * 2;
        const drawH = this.height * 2;
        const hLeft = this.x - cameraX;
        const hTop = this.y;
        const hRight = hLeft + drawW;
        const hBottom = hTop + drawH;

        const pLeft = prince.x + 55;
        const pTop = prince.y + 50;
        const pRight = pLeft + 80;
        const pBottom = pTop + 150;

        const overlapping =
            pLeft < hRight &&
            pRight > hLeft &&
            pTop < hBottom &&
            pBottom > hTop;

        if (overlapping) {
            if (!this.hasHitPrince) {
                if (prince.currentAnimation === 'attackLeft' ||
                    prince.currentAnimation === 'attackRight') {
                    this.alive = false;
                    return;
                } else {
                    lives.lives = Math.max(0, lives.lives - 1);
                    this.hasHitPrince = true;
                }
            }
        } else {
            this.hasHitPrince = false;
        }

    }

    draw(ctx) {
        if (!this.alive) return;
        const frameW = this.width / this.numberOfFrames;

        ctx.drawImage(
            this.spritesheet,
            this.frameIndex * frameW, 0,
            frameW, this.height,
            this.x - cameraX, this.y,
            frameW * 2, this.height * 2
        );
    }
}

// animates a burning trap that hurts the prince when overlapping its hitbox
class FireTrap extends Sprite {
    constructor(imagePath, x, y, width, height) {
        super();
        var fireTrapSpritesheet = new Image();
        fireTrapSpritesheet.src = imagePath;
        this.spritesheet = fireTrapSpritesheet;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.numberOfFrames = 11;
        this.frameIndex = 0;
        this.counter = 0;
        this.hasHitPrince = false;
    }

    update() {
        if (paused) return;

        this.counter++;
        if (this.counter >= 10) {
            this.counter = 0;
            this.frameIndex++;
            if (this.frameIndex >= this.numberOfFrames) {
                this.frameIndex = 0;
            }
        }

        const frameW = this.width / this.numberOfFrames;
        const drawW = frameW * 2;
        const drawH = this.height * 2;
        const ftLeft = this.x - cameraX + 60;
        const ftTop = this.y + 30;
        const ftRight = ftLeft + drawW - 110;
        const ftBottom = ftTop + drawH;

        const pLeft = prince.x + 55;
        const pTop = prince.y + 50;
        const pRight = pLeft + 80;
        const pBottom = pTop + 150;

        const overlapping =
            pLeft < ftRight &&
            pRight > ftLeft &&
            pTop < ftBottom &&
            pBottom > ftTop;


        if (overlapping && !this.hasHitPrince) {
            // damage prince once per overlap
            lives.lives = Math.max(0, lives.lives - 1);
            this.hasHitPrince = true;
        } else if (!overlapping)
            this.hasHitPrince = false;
    }

    draw(ctx) {
        const frameW = this.width / this.numberOfFrames;

        ctx.drawImage(
            this.spritesheet,
            this.frameIndex * frameW, 0,
            frameW, this.height,
            this.x - cameraX, this.y,
            frameW * 2, this.height * 2
        );
    }
}

// a beast that moves left, animates, and interacts with the prince on collision
class Dragon extends Sprite {
    constructor(imagePath, x, y, width, height) {
        super();
        var dragonSpritesheet = new Image();
        dragonSpritesheet.src = imagePath;
        this.spritesheet = dragonSpritesheet;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.numberOfFrames = 5;
        this.frameIndex = 0;
        this.counter = 0;
        this.speed = 2;     // horizontal movement speed
        this.hasHitPrince = false;      // to avoid multiple hits per collision
        this.alive = true;      // toggles false when defeated
    }

    update(sprites, keys) {
        if (paused) return;

        if (!this.alive) {
            return;
        }

        this.counter++;
        if (this.counter >= 4) {
            this.counter = 0;
            this.frameIndex++;
            if (this.frameIndex >= this.numberOfFrames) {
                this.frameIndex = 0;
            }
        }

        this.x -= this.speed;
        const frameW = this.width / this.numberOfFrames;
        const drawW = frameW * 2;
        const drawH = this.height * 2;
        const drLeft = this.x - cameraX;
        const drTop = this.y;
        const drRight = drLeft + drawW;
        const drBottom = drTop + drawH;

        const pLeft = prince.x + 55;
        const pTop = prince.y + 50;
        const pRight = pLeft + 80;
        const pBottom = pTop + 150;

        const overlapping =
            pLeft < drRight &&
            pRight > drLeft &&
            pTop < drBottom &&
            pBottom > drTop;

        if (overlapping) {
            if (!this.hasHitPrince) {
                if (prince.currentAnimation === 'attackLeft' || prince.currentAnimation === 'attackRight') {
                    this.alive = false;
                    return;
                } else {
                    lives.lives = Math.max(0, lives.lives - 1);
                    this.hasHitPrince = true;
                }
            }
        } else
            this.hasHitPrince = false;
    }

    // draw current animation frame if still alive
    draw(ctx) {
        if (!this.alive) return;

        const frameW = this.width / this.numberOfFrames;

        ctx.drawImage(
            this.spritesheet,
            this.frameIndex * frameW, 0,
            frameW, this.height,
            this.x - cameraX, this.y,
            frameW * 2, this.height * 2
        );
    }
}

// a punching mechanism that cycles frames slowly and harms the prince on contact
class PunchTrap extends Sprite {
    constructor(imagePath, x, y, width, height) {
        super();
        var punchTrapSpritesheet = new Image();
        punchTrapSpritesheet.src = imagePath;
        this.spritesheet = punchTrapSpritesheet;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.numberOfFrames = 16;
        this.frameIndex = 0;
        this.counter = 0;
        this.hasHitPrince = false;
    }
    update() {
        if (paused) return;

        this.counter++;
        if (this.counter >= 15) {
            this.counter = 0;
            this.frameIndex++;
            if (this.frameIndex >= this.numberOfFrames) {
                this.frameIndex = 0;
            }
        }

        const frameW = this.width / this.numberOfFrames;
        const drawW = frameW * 2;
        const drawH = this.height * 2;
        const ptLeft = this.x - cameraX + 60;
        const ptTop = this.y + 30;
        const ptRight = ptLeft + drawW - 110;
        const ptBottom = ptTop + drawH;

        const pLeft = prince.x + 55;
        const pTop = prince.y + 50;
        const pRight = pLeft + 80;
        const pBottom = pTop + 150;

        const overlapping =
            pLeft < ptRight &&
            pRight > ptLeft &&
            pTop < ptBottom &&
            pBottom > ptTop;


        if (overlapping && !this.hasHitPrince) {
            lives.lives = Math.max(0, lives.lives - 1);
            this.hasHitPrince = true;
        } else if (!overlapping)
            this.hasHitPrince = false;
    }

    draw(ctx) {
        const frameW = this.width / this.numberOfFrames;

        ctx.drawImage(
            this.spritesheet,
            this.frameIndex * frameW, 0,
            frameW, this.height,
            this.x - cameraX, this.y,
            frameW * 2, this.height * 2
        );
    }
}

// a ground-dwelling demon that stalks left, can be slain by prince’s attack
class Demon extends Sprite {
    constructor(imagePath, x, y, width, height) {
        super();
        var dragonSpritesheet = new Image();
        dragonSpritesheet.src = imagePath;
        this.spritesheet = dragonSpritesheet;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.numberOfFrames = 6;
        this.frameIndex = 0;
        this.counter = 0;
        this.speed = 2;
        this.hasHitPrince = false;
        this.alive = true;
    }

    update(sprites, keys) {
        if (paused) return;

        if (!this.alive) {
            return;
        }

        this.counter++;
        if (this.counter >= 5) {
            this.counter = 0;
            this.frameIndex++;
            if (this.frameIndex >= this.numberOfFrames) {
                this.frameIndex = 0;
            }
        }

        this.x -= this.speed;

        const frameW = this.width / this.numberOfFrames;
        const drawW = frameW * 2;
        const drawH = this.height * 2;
        const deLeft = this.x - cameraX;
        const deTop = this.y;
        const deRight = deLeft + drawW;
        const deBottom = deTop + drawH;

        const pLeft = prince.x + 55;
        const pTop = prince.y + 50;
        const pRight = pLeft + 80;
        const pBottom = pTop + 150;

        const overlapping =
            pLeft < deRight &&
            pRight > deLeft &&
            pTop < deBottom &&
            pBottom > deTop;

        if (overlapping) {
            if (!this.hasHitPrince) {
                if (prince.currentAnimation === 'attackLeft' || prince.currentAnimation === 'attackRight') {
                    console.log('ATTACK! killing skeleton at x=', this.x);
                    this.alive = false;
                    return;
                } else {
                    lives.lives = Math.max(0, lives.lives - 1);
                    this.hasHitPrince = true;
                }
            }
        } else
            this.hasHitPrince = false;
    }

    draw(ctx) {
        if (!this.alive) return;

        const frameW = this.width / this.numberOfFrames;

        ctx.drawImage(
            this.spritesheet,
            this.frameIndex * frameW, 0,
            frameW, this.height,
            this.x - cameraX, this.y,
            frameW * 2, this.height * 2
        );
    }
}

// grants the player extra life when collected
class HealthBooster extends Sprite {
    constructor(imagePath, x, y, width, height) {
        super();
        var healthBoosterSpritesheet = new Image();
        healthBoosterSpritesheet.src = imagePath;
        this.spritesheet = healthBoosterSpritesheet;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.numberOfFrames = 8;
        this.frameIndex = 0;
        this.counter = 0;
        this.pickedUp = false;      // pickup flag to disable after collected
    }

    update() {
        if (paused) return;     // don't animate or collide when paused

        if (this.pickedUp) return;      // once picked up, remove from play

        this.counter++;
        if (this.counter >= 7) {
            this.counter = 0;
            this.frameIndex++;
            if (this.frameIndex >= this.numberOfFrames) {
                this.frameIndex = 0;
            }
        }

        const frameW = this.width / this.numberOfFrames;
        const drawW = frameW;
        const drawH = this.height;
        const hbLeft = this.x - cameraX;
        const hbTop = this.y;
        const hbRight = hbLeft + drawW;
        const hbBottom = hbTop + drawH;

        const pLeft = prince.x + 55;
        const pTop = prince.y + 50;
        const pRight = pLeft + 80;
        const pBottom = pTop + 150;

        const overlapping =
            pLeft < hbRight &&
            pRight > hbLeft &&
            pTop < hbBottom &&
            pBottom > hbTop;

        if (overlapping) {
            if (lives.lives < maxLives) {
                lives.lives++;
            }
            this.pickedUp = true;
        }
    }

    // renders current frame if not yet picked
    draw(ctx) {
        if (this.pickedUp) return;

        const frameW = this.width / this.numberOfFrames;

        ctx.drawImage(
            this.spritesheet,
            this.frameIndex * frameW, 0,
            frameW, this.height,
            this.x - cameraX, this.y,
            frameW, this.height
        );
    }
}

class Button extends Sprite {
    constructor(x, y, width, height, text, onClick) {
        super();
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.text = text;
        this.onClick = onClick;
        this.isHovered = false;
    }

    update(sprites, keys, mouse) {
        this.isHovered = this.isMouseOver(mouse);

        if (mouse.clicked && this.isHovered) {
            this.onClick();
            mouse.clicked = false;
        }
    }

    isMouseOver(mouse) {
        if (mouse == undefined) return false;
        return (
            mouse.x >= this.x &&
            mouse.x <= this.x + this.width &&
            mouse.y >= this.y &&
            mouse.y <= this.y + this.height
        );
    }

    draw(ctx) {
        ctx.fillStyle = this.isHovered ? "#666" : "#333"; // Darker color when hovered
        ctx.fillRect(this.x, this.y, this.width, this.height);
        ctx.fillStyle = "white";
        ctx.font = "20px Arial";
        ctx.textAlign = "center";
        ctx.textBaseline = "middle";
        ctx.fillText(this.text, this.x + this.width / 2, this.y + this.height / 2);
    }
}

class Message extends Sprite {
    constructor(text, x, y, color) {
        super();
        this.text = text;
        this.x = x;
        this.y = y;
        this.color = color;
    }

    draw(ctx) {
        ctx.font = "20px Arial";
        ctx.fillStyle = this.color;
        ctx.fillText(this.text, this.x, this.y);
    }
}

class Sound extends Sprite {
    constructor(src, volume = 1, loop = false) {
        super();
        this.audio = new Audio(src);
        this.audio.volume = volume;
        this.audio.loop = loop;
    }

    update() { }

    draw() { }

    // play from start 
    play() {
        this.audio.currentTime = 0;
        this.audio.play();
    }

    // pause playback 
    pause() {
        this.audio.pause();
    }
}

// main menu screen with "Start Game" and "Exit"
class LevelMenu extends Level {
    constructor(game) {
        super();
        this.game = game;
    }

    initialize() {
        this.game.addSprite(new BackgroundSprite2("images/menu/castleMenu.png"));     // image for the menu
        this.game.addSprite(new Message("Prince's Pursuit", GAME_WIDTH / 2, GAME_HEIGHT / 2 - 200, "white"));      // title text at top center
        // "Start Game" button: when clicked, switch to level index 1
        this.game.addSprite(new Button(GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2, 200, 50, "Start Game", () => this.game.changeLevel(1)));
        // "Exit" button: show alert
        this.game.addSprite(new Button(GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2 + 70, 200, 50, "Exit", () => alert("Game Closed!")));
    }
}

// instructions screen before gameplay
class LevelInstructions extends Level {
    constructor(game) {
        super();
        this.game = game;
    }

    initialize() {
        this.game.addSprite(new BackgroundSprite2("images/instructions/instructions.png"));
        // multiple Message sprites, each on its own line, centered
        this.game.addSprite(new Message("Prince's Pursuit is a game based on controlling a prince navigating through", GAME_WIDTH / 2, GAME_HEIGHT / 2 - 200, "white"));
        this.game.addSprite(new Message("increasingly difficult obstacles to rescue the princess from her abductors", GAME_WIDTH / 2, GAME_HEIGHT / 2 - 160, "white"));
        this.game.addSprite(new Message("Press <- or -> to move left and right respectively,", GAME_WIDTH / 2, GAME_HEIGHT / 2 - 90, "white"));
        this.game.addSprite(new Message("Press A or S to attack left and right respectively,", GAME_WIDTH / 2, GAME_HEIGHT / 2 - 50, "white"));
        this.game.addSprite(new Message("Press P to pause and C to continue,", GAME_WIDTH / 2, GAME_HEIGHT / 2 - 10, "white"));
        this.game.addSprite(new Message("and SPACEBAR to jump!!", GAME_WIDTH / 2, GAME_HEIGHT / 2 + 30, "white"));
        // "Start Game" button to move to level 2 (which is the firstn playable level)
        this.game.addSprite(new Button(GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2 + 60, 200, 50, "Start Game", () => this.game.changeLevel(2)));
        this.game.addSprite(new Button(GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2 + 130, 200, 50, "Back to Menu", () => this.game.changeLevel(0)));
    }
}

// forest world, initial playable level
class Level1 extends Level {
    constructor(game) {
        super();
        this.game = game;
    }

    initialize() {
        cameraX = 0;    // reset horizontal camera offset

        // build parallax background from forest layers
        layerSourcesForest.forEach(layer => {
            const img = new Image();
            img.src = layer.src;
            this.game.addSprite(new BackgroundSprite(img, layer.speed));
        })

        // lives UI in top left
        const livesSprite = new Lives(5, 'images/lives/heart.png', 32);
        this.game.addSprite(livesSprite);
        lives = livesSprite;

        // prince sprite
        const princeSprite = new Prince();
        this.game.addSprite(princeSprite);
        prince = princeSprite;

        this.game.addSprite(new Skeleton("images/skeleton/walk left.png", LEVEL_WIDTH + 50, 353, 7 * 128, 94, 7));
        this.game.addSprite(new Skeleton("images/skeleton/walk left.png", LEVEL_WIDTH + 750, 353, 7 * 128, 94, 7));
        this.game.addSprite(new Hyena("images/hyena/walk left.png", LEVEL_WIDTH - 800, 450, 6 * 48, 48, 6));
        this.game.addSprite(new Hyena("images/hyena/walk left.png", LEVEL_WIDTH + 1300, 450, 6 * 48, 48, 6));
        this.game.addSprite(new FireTrap("images/fireTrap/fireTrap.png", 500, 415, 11 * 96, 64, 11));
        this.game.addSprite(new FireTrap("images/fireTrap/fireTrap.png", LEVEL_WIDTH - 600, 415, 11 * 96, 64, 11));
        this.game.addSprite(new HealthBooster("images/healthBooster/healthBooster.png", 800, 505, 8 * 22, 37, 8));
        this.game.addSprite(new HealthBooster("images/healthBooster/healthBooster.png", 1600, 505, 8 * 22, 37, 8));
        this.game.addSprite(new HealthBooster("images/healthBooster/healthBooster.png", 3000, 505, 8 * 22, 37, 8));
    }
}

// cave world, intermediate level
class Level2 extends Level {
    constructor(game) {
        super();
        this.game = game;
    }

    initialize() {
        cameraX = 0;    // always reset camera at start of level

        layerSourcesCave.forEach(layer => {
            const img = new Image();
            img.src = layer.src;
            this.game.addSprite(new BackgroundSprite(img, layer.speed));
        })

        const livesSprite = new Lives(5, 'images/lives/heart.png', 32);
        this.game.addSprite(livesSprite);
        lives = livesSprite;

        const princeSprite = new Prince();
        this.game.addSprite(princeSprite);
        prince = princeSprite;

        this.game.addSprite(new Skeleton("images/skeleton/walk left.png", LEVEL_WIDTH + 50, 353, 7 * 128, 94, 7));
        this.game.addSprite(new Skeleton("images/skeleton/walk left.png", LEVEL_WIDTH + 750, 353, 7 * 128, 94, 7));
        this.game.addSprite(new Hyena("images/hyena/walk left.png", LEVEL_WIDTH - 800, 450, 6 * 48, 48, 6));
        this.game.addSprite(new Hyena("images/hyena/walk left.png", LEVEL_WIDTH + 1300, 450, 6 * 48, 48, 6));
        this.game.addSprite(new FireTrap("images/fireTrap/fireTrap.png", 600, 415, 11 * 96, 64, 11));
        this.game.addSprite(new FireTrap("images/fireTrap/fireTrap.png", LEVEL_WIDTH - 600, 415, 11 * 96, 64, 11));
        this.game.addSprite(new Dragon("images/dragon/walk left.png", LEVEL_WIDTH - 1200, 400, 5 * 150, 77, 5));
        this.game.addSprite(new Dragon("images/dragon/walk left.png", LEVEL_WIDTH + 500, 400, 5 * 150, 77, 5));
        this.game.addSprite(new PunchTrap("images/punchTrap/punchTrap.png", 400, 477, 16 * 96, 32, 16));
        this.game.addSprite(new PunchTrap("images/punchTrap/punchTrap.png", LEVEL_WIDTH - 1100, 477, 16 * 96, 32, 16));
        this.game.addSprite(new PunchTrap("images/punchTrap/punchTrap.png", LEVEL_WIDTH - 400, 477, 16 * 96, 32, 16));
        this.game.addSprite(new HealthBooster("images/healthBooster/healthBooster.png", 800, 505, 8 * 22, 37, 8));
        this.game.addSprite(new HealthBooster("images/healthBooster/healthBooster.png", 1600, 505, 8 * 22, 37, 8));
    }
}

// castle world, final playable level
class Level3 extends Level {
    constructor(game) {
        super();
        this.game = game;
    }

    initialize() {
        cameraX = 0;

        layerSourcesCastle.forEach(layer => {
            const img = new Image();
            img.src = layer.src;
            this.game.addSprite(new BackgroundSprite(img, layer.speed));
        })

        const livesSprite = new Lives(5, 'images/lives/heart.png', 32);
        this.game.addSprite(livesSprite);
        lives = livesSprite;

        const princeSprite = new Prince();
        this.game.addSprite(princeSprite);
        prince = princeSprite;

        this.game.addSprite(new Skeleton("images/skeleton/walk left.png", LEVEL_WIDTH + 50, 353, 7 * 128, 94, 7));
        this.game.addSprite(new Skeleton("images/skeleton/walk left.png", LEVEL_WIDTH + 750, 353, 7 * 128, 94, 7));
        this.game.addSprite(new Hyena("images/hyena/walk left.png", LEVEL_WIDTH - 800, 450, 6 * 48, 48, 6));
        this.game.addSprite(new Hyena("images/hyena/walk left.png", LEVEL_WIDTH + 1300, 450, 6 * 48, 48, 6));
        this.game.addSprite(new FireTrap("images/fireTrap/fireTrap.png", 600, 415, 11 * 96, 64, 11));
        this.game.addSprite(new FireTrap("images/fireTrap/fireTrap.png", LEVEL_WIDTH - 600, 415, 11 * 96, 64, 11));
        this.game.addSprite(new Dragon("images/dragon/walk left.png", LEVEL_WIDTH - 1200, 400, 5 * 150, 77, 5));
        this.game.addSprite(new Dragon("images/dragon/walk left.png", LEVEL_WIDTH + 500, 400, 5 * 150, 77, 5));
        this.game.addSprite(new PunchTrap("images/punchTrap/punchTrap.png", 400, 477, 16 * 96, 32, 16));
        this.game.addSprite(new PunchTrap("images/punchTrap/punchTrap.png", LEVEL_WIDTH - 1100, 477, 16 * 96, 32, 16));
        this.game.addSprite(new PunchTrap("images/punchTrap/punchTrap.png", LEVEL_WIDTH - 400, 477, 16 * 96, 32, 16));
        this.game.addSprite(new HealthBooster("images/healthBooster/healthBooster.png", 800, 505, 8 * 22, 37, 8));
        this.game.addSprite(new Demon("images/demon/walk left.png", LEVEL_WIDTH - 2000, 360, 6 * 81, 94, 6));
        this.game.addSprite(new HealthBooster("images/healthBooster/healthBooster.png", 1600, 505, 8 * 22, 37, 8));
        this.game.addSprite(new Princess(LEVEL_WIDTH - 200, GAME_HEIGHT / 2 + 50, 'inLevel'));

    }
}

// displayed when the player dies on level 1
class LoseLevel1 extends Level {
    constructor(game) {
        super();
        this.game = game;
    }

    // called once when transitioning into this “lose” screen
    // reuses the forest background, then overlays text and buttons
    initialize() {
        // redraw parallax forest layers in static form 
        layerSourcesForest.forEach(layer => {
            const img = new Image();
            img.src = layer.src;
            this.game.addSprite(new BackgroundSprite(img, layer.speed));
        });

        this.game.addSprite(new Message("GAME OVER!! YOU LOST", GAME_WIDTH / 2, GAME_HEIGHT / 2 - 40, "red"));
        this.game.addSprite(new Button(GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2, 200, 50, "Try Again", () => this.game.changeLevel(2)));
        this.game.addSprite(new Button(GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2 + 70, 200, 50, "Back to Menu", () => this.game.changeLevel(0)));
    }
}

// displayed when the player wins level 1
class WinLevel1 extends Level {
    constructor(game) {
        super();
        this.game = game;
    }

    // show the “You Won” screen over the forest background
    // provides “Next Level” and “Back to Menu” options
    initialize() {
        layerSourcesForest.forEach(layer => {
            const img = new Image();
            img.src = layer.src;
            this.game.addSprite(new BackgroundSprite(img, layer.speed));
        })

        this.game.addSprite(new Message("YOU WON!!", GAME_WIDTH / 2, GAME_HEIGHT / 2 - 40, "white"));
        this.game.addSprite(new Button(GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2, 200, 50, "Next Level", () => this.game.changeLevel(3)));
        this.game.addSprite(new Button(GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2 + 70, 200, 50, "Back to Menu", () => this.game.changeLevel(0)));
    }
}

// displayed when the player dies on level 2
class LoseLevel2 extends Level {
    constructor(game) {
        super();
        this.game = game;
    }

    // called once when transitioning into this “lose” screen
    // reuses the cave background, then overlays text and buttons
    initialize() {
        // redraw parallax cave layers in static form 
        layerSourcesCave.forEach(layer => {
            const img = new Image();
            img.src = layer.src;
            this.game.addSprite(new BackgroundSprite(img, layer.speed));
        });

        this.game.addSprite(new Message("GAME OVER!! YOU LOST", GAME_WIDTH / 2, GAME_HEIGHT / 2 - 40, "red"));
        this.game.addSprite(new Button(GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2, 200, 50, "Try Again", () => this.game.changeLevel(3)));
        // “Back to Menu” button: sends back to main menu (index 0)        
        this.game.addSprite(new Button(GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2 + 70, 200, 50, "Back to Menu", () => this.game.changeLevel(0)));
    }
}

// displayed when the player wins level 2
class WinLevel2 extends Level {
    constructor(game) {
        super();
        this.game = game;
    }

    initialize() {
        layerSourcesCave.forEach(layer => {
            const img = new Image();
            img.src = layer.src;
            this.game.addSprite(new BackgroundSprite(img, layer.speed));
        })

        this.game.addSprite(new Message("YOU WON!!", GAME_WIDTH / 2, GAME_HEIGHT / 2 - 40, "white"));
        this.game.addSprite(new Button(GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2, 200, 50, "Next Level", () => this.game.changeLevel(4)));
        this.game.addSprite(new Button(GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2 + 70, 200, 50, "Back to Menu", () => this.game.changeLevel(0)));
    }
}

// displayed when the player dies on level 3
class LoseLevel3 extends Level {
    constructor(game) {
        super();
        this.game = game;
    }

    // called once when transitioning into this “lose” screen
    // reuses the castle background, then overlays text and buttons
    initialize() {
        // redraw parallax castle layers in static form 
        layerSourcesCastle.forEach(layer => {
            const img = new Image();
            img.src = layer.src;
            this.game.addSprite(new BackgroundSprite(img, layer.speed));
        });

        this.game.addSprite(new Message("YOU HAVE FAILED AT SAVING THE PRINCESS!!", GAME_WIDTH / 2, GAME_HEIGHT / 2 - 40, "red"));
        const princess = new Princess(GAME_WIDTH / 2 - 32, GAME_HEIGHT / 2, 'lose');    // princess in "lose" animation state
        this.game.addSprite(princess)
        // “Back to Menu” button: sends back to main menu (index 0)        
        this.game.addSprite(new Button(GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2 + 150, 200, 50, "Back to Menu", () => this.game.changeLevel(0)));
    }
}

// displayed when the player wins level 3 (when the prince rescues the princess)
class WinLevel3 extends Level {
    constructor(game) {
        super();
        this.game = game;
    }

    initialize() {
        layerSourcesCastle.forEach(layer => {
            const img = new Image();
            img.src = layer.src;
            this.game.addSprite(new BackgroundSprite(img, layer.speed));
        })

        this.game.addSprite(new Message("YOU HAVE SUCCESSFULLY SAVED THE PRINCESS!!", GAME_WIDTH / 2, GAME_HEIGHT / 2 - 30, "white"));
        const princess = new Princess(GAME_WIDTH / 2 - 32, GAME_HEIGHT / 2, 'win');     // princess in “win” animation state
        this.game.addSprite(princess);
        this.game.addSprite(new Button(GAME_WIDTH / 2 - 100, GAME_HEIGHT / 2 + 150, 200, 50, "Back to Menu", () => this.game.changeLevel(0)));
    }
}

class BackgroundSprite2 extends Sprite {
    constructor(imageSrc) {
        super();
        this.image = new Image();
        this.image.src = imageSrc;
    }

    draw(ctx) {
        ctx.drawImage(this.image, 0, 0, ctx.canvas.width, ctx.canvas.height);
    }
}

const game = new Game();
const bgMusic = new Sound('audio/backgroundMusic.mp3', 0.2, true);      // create background music, set volume to 20%, and enable looping
const victory = new Sound('audio/victory.mp3', 0.6, false);     // create victory sound effect, 60% volume, no looping
const loss = new Sound('audio/loss.mp3', 0.6, false);       // create loss sound effect, 60% volume, no looping
const menu = new LevelMenu(game);       // main menu screen (level index 0)
const instructions = new LevelInstructions(game);       // instructions screen (level index 1)
const l1 = new Level1(game);      // level 1 (forest) at index 2
const l2 = new Level2(game);      // level 2 (cave) at index 3
const l3 = new Level3(game);      // level 3 (castle) at index 4
const lose1 = new LoseLevel1(game);     // lose screen for level 1 at index 5
const win1 = new WinLevel1(game);       // win screen for level 1 at index 6
const lose2 = new LoseLevel2(game);     // lose screen for level 2 at index 7
const win2 = new WinLevel2(game);       // win screen for level 2 at index 8
const lose3 = new LoseLevel3(game);     // lose screen for level 3 at index 9
const win3 = new WinLevel3(game);       // win screen for level 3 at index 10
l1.loseLevel = 5;       // when prince loses level 1, switch to index 5
l1.winLevel = 6;        // when prince wins level 1, switch to index 6
l2.loseLevel = 7;       // when prince loses level 2, switch to index 7
l2.winLevel = 8;        // when prince wins level 2, switch to index 8
l3.loseLevel = 9;       // when prince loses level 3, switch to index 9
l3.winLevel = 10;       // when prince wins level 3, switch to index 10

// register all levels with the game in their correct order
game.addLevel(menu);
game.addLevel(instructions);
game.addLevel(l1);
game.addLevel(l2);
game.addLevel(l3);
game.addLevel(lose1);
game.addLevel(win1);
game.addLevel(lose2);
game.addLevel(win2);
game.addLevel(lose3);
game.addLevel(win3);

game.animate();