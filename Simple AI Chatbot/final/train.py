from transformers import (
    GPT2LMHeadModel,    # GPT-2 model with a language modeling head (for text generation)
    GPT2Tokenizer,      # Tokenizer that converts text to tokens for GPT-2
    TextDataset,        # Utility for loading plain text datasets
    DataCollatorForLanguageModeling,    # Helps format data correctly during training
    Trainer,    # Simplifies training logic
    TrainingArguments       # Holds configuration options for training
)

# Step 1: Define which model to use
def fine_tune_gpt2():
    model_name = "gpt2"     # Using the base GPT-2 model from Hugging Face

    # Step 2: Load the tokenizer
    tokenizer = GPT2Tokenizer.from_pretrained(model_name)
    tokenizer.pad_token = tokenizer.eos_token   # Set the padding token to the end-of-sequence token (important for GPT-2)

    # Step 3: Load the pretrained GPT-2 model
    model = GPT2LMHeadModel.from_pretrained(model_name)

    # Load your dataset
    dataset = TextDataset(
        tokenizer=tokenizer,    # Tokenizer to convert text into tokens
        file_path="C:/Users/celin/PycharmProjects/final/bakery_data.txt",
        block_size=128      # Max sequence length (how many tokens per training block)
    )

    # Step 5: Set up data collator
    data_collator = DataCollatorForLanguageModeling(
        tokenizer=tokenizer,    # Ensures inputs are correctly padded and formatted
        mlm=False        # Set to False because GPT-2 is not trained with masked language modeling
    )

    # Step 6: Define training parameters
    training_args = TrainingArguments(
        output_dir="./gpt2-bakery",         # Where to save the trained model
        overwrite_output_dir=True,          # Overwrite existing content in the output folder
        num_train_epochs=3,                 # Number of passes over the dataset
        per_device_train_batch_size=2,      # How many samples to process at once per device (small for low memory use)
        save_steps=500,                     # Save model every 500 steps
        save_total_limit=1,                 # Keep only 1 version of the model saved (older ones are deleted)
        prediction_loss_only=True,          # Don’t compute evaluation metrics, just focus on training loss
        logging_steps=50                    # Print training progress every 50 steps
    )

    # Step 7: Set up the trainer
    trainer = Trainer(
        model=model,                        # Model to train
        args=training_args,                 # Training configuration
        data_collator=data_collator,        # Handles batch formatting and padding
        train_dataset=dataset               # The tokenized training dataset
    )

    # Step 8: Start training the model
    print("Starting training...")
    trainer.train()

    # Step 9: Save the trained model and tokenizer locally
    print("Saving model...")
    model.save_pretrained("./gpt2-bakery")      # Save the model weights and config
    tokenizer.save_pretrained("./gpt2-bakery")      # Save tokenizer settings (vocab, special tokens)

# This makes sure the script runs only when executed directly
if __name__ == "__main__":
    fine_tune_gpt2()
