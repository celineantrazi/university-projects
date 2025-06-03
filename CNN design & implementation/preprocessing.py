import tensorflow as tf
import os
import matplotlib.pyplot as plt
import numpy as np
from sklearn.metrics import confusion_matrix

# ── Params ────────────────────────────────────────────────────────────────
BASE_DIR   = os.path.dirname(__file__)
DATA_DIR   = os.path.join(BASE_DIR, "train")   # adjust if needed
IMG_SIZE   = (224, 224)
BATCH_SIZE = 32
AUTOTUNE   = tf.data.AUTOTUNE
EPOCHS     = 10

# ── 1) Load & Split ────────────────────────────────────────────────────────
raw_train_ds = tf.keras.utils.image_dataset_from_directory(
    DATA_DIR,
    labels='inferred',
    label_mode='int',
    validation_split=0.2,
    subset='training',
    seed=42,
    image_size=IMG_SIZE,
    batch_size=BATCH_SIZE,
    shuffle=True
)
raw_val_ds = tf.keras.utils.image_dataset_from_directory(
    DATA_DIR,
    labels='inferred',
    label_mode='int',
    validation_split=0.2,
    subset='validation',
    seed=42,
    image_size=IMG_SIZE,
    batch_size=BATCH_SIZE,
    shuffle=False
)

# Capture class names BEFORE any dataset transformations
class_names = raw_train_ds.class_names
print("Classes:", class_names)

# ── 2) Augmentation & Normalization ────────────────────────────────────────
data_augmentation = tf.keras.Sequential([
    tf.keras.layers.RandomFlip("horizontal"),
    tf.keras.layers.RandomRotation(0.2),
    tf.keras.layers.RandomZoom(0.1),
], name="augmentation")

normalization = tf.keras.layers.Rescaling(1.0/255, name="rescale")

def preprocess_train(image, label):
    image = data_augmentation(image)
    image = normalization(image)
    return image, label

def preprocess_val(image, label):
    image = normalization(image)
    return image, label

train_ds = (raw_train_ds
    .map(preprocess_train, num_parallel_calls=AUTOTUNE)
    .cache()
    .shuffle(1000)
    .prefetch(AUTOTUNE)
)
val_ds = (raw_val_ds
    .map(preprocess_val, num_parallel_calls=AUTOTUNE)
    .cache()
    .prefetch(AUTOTUNE)
)

# ── 3) Model Definition ──────────────────────────────────────────────────────
model = tf.keras.Sequential([
    tf.keras.layers.Input(shape=(*IMG_SIZE, 3)),
    tf.keras.layers.Conv2D(32, 3, padding='same', activation='relu'),
    tf.keras.layers.MaxPool2D(),
    tf.keras.layers.Conv2D(64, 3, padding='same', activation='relu'),
    tf.keras.layers.MaxPool2D(),
    tf.keras.layers.Conv2D(128, 3, padding='same', activation='relu'),
    tf.keras.layers.MaxPool2D(),
    tf.keras.layers.Flatten(),
    tf.keras.layers.Dense(128, activation='relu'),
    tf.keras.layers.Dropout(0.5),
    tf.keras.layers.Dense(len(class_names), activation='softmax'),
])

# ── 4) Compile with AdamW & Sparse Categorical Crossentropy ───────────────
model.compile(
    optimizer=tf.keras.optimizers.AdamW(learning_rate=1e-3),
    loss='sparse_categorical_crossentropy',
    metrics=['accuracy']
)

# ── 5) Train ────────────────────────────────────────────────────────────────
history = model.fit(
    train_ds,
    validation_data=val_ds,
    epochs=EPOCHS,
    verbose=2
)

# ── 6) Plot Accuracy & Loss ─────────────────────────────────────────────────
epochs_range = range(1, EPOCHS + 1)

plt.figure(figsize=(12, 4))
# Accuracy
plt.subplot(1, 2, 1)
plt.plot(epochs_range, history.history['accuracy'],    label='Train Acc')
plt.plot(epochs_range, history.history['val_accuracy'],label='Val Acc')
plt.title('Accuracy over Epochs')
plt.xlabel('Epoch'); plt.ylabel('Accuracy')
plt.legend()

# Loss
plt.subplot(1, 2, 2)
plt.plot(epochs_range, history.history['loss'],    label='Train Loss')
plt.plot(epochs_range, history.history['val_loss'],label='Val Loss')
plt.title('Loss over Epochs')
plt.xlabel('Epoch'); plt.ylabel('Loss')
plt.legend()

plt.tight_layout()
plt.show()

# ── 7) Final Validation Accuracy ─────────────────────────────────────────────
final_val_acc = history.history['val_accuracy'][-1] * 100
print(f"Final validation accuracy: {final_val_acc:.2f}%")

# ── 8) Compute predictions on the validation set ────────────────────────────────────────────
y_true = np.concatenate([y.numpy() for x, y in val_ds], axis=0)
y_pred_probs = model.predict(val_ds)
y_pred = np.argmax(y_pred_probs, axis=1)

# ── 9) Build confusion matrix ────────────────────────────────────────────
cm = confusion_matrix(y_true, y_pred)

# ── 10) Plot confusion matrix ────────────────────────────────────────────
plt.figure()
plt.imshow(cm)
plt.title('Confusion Matrix')
plt.xlabel('Predicted Label')
plt.ylabel('True Label')
plt.xticks(np.arange(len(class_names)), class_names, rotation=45, ha='right')
plt.yticks(np.arange(len(class_names)), class_names)
for i in range(cm.shape[0]):
    for j in range(cm.shape[1]):
        plt.text(j, i, cm[i, j], ha='center', va='center')
plt.tight_layout()
plt.show()