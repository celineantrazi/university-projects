from transformers import GPT2LMHeadModel, GPT2Tokenizer     # For loading the model and tokenizer
import torch       # PyTorch handles the tensors and computations

# Load the fine-tuned GPT-2 model and tokenizer from the local directory
model_path = "./gpt2-bakery"      # This points to the directory where the trained model is saved
tokenizer = GPT2Tokenizer.from_pretrained(model_path)       # Loads the custom tokenizer
model = GPT2LMHeadModel.from_pretrained(model_path)     # Loads the fine-tuned GPT-2 model

# Welcome message
print("🍰 Ruby's Goodies Bot is ready! (type 'exit' to quit)\n")

# Start the chatbot loop
while True:
    user_input = input("You: ")     # Ask user for a question
    if user_input.lower() in ["exit", "quit"]:      # Stop the loop if user types 'exit' or 'quit'
        break

    # Prepare the prompt in the same format the model was trained on
    prompt = f"Q: {user_input}\nA:"     # Add 'Q:' before user input and start 'A:' for model to complete

    # Tokenize the prompt and convert it into a tensor (model input format)
    input_ids = tokenizer.encode(prompt, return_tensors="pt")      # Converts prompt into token IDs
    attention_mask = torch.ones_like(input_ids)  # Prevents warning about missing attention mask

    # Generate a response using the model
    output = model.generate(
        input_ids,                                  # The tokenized prompt
        attention_mask=attention_mask,              # Ensures all tokens are attended to
        max_length=input_ids.shape[1] + 50,         # Allow up to 50 new tokens to be generated
        pad_token_id=tokenizer.eos_token_id,        # Use end-of-sequence token for padding
        do_sample=True,                             # Enables sampling (non-deterministic/random output)
        temperature=0.7,                            # Controls randomness (lower is more conservative)
        top_k=50,                                   # Consider only the top 50 tokens at each step
        top_p=0.95,                                 # Use nucleus sampling (top 95% probability mass)
        eos_token_id=tokenizer.encode("\nQ:")[0]    # Stop generation if a new question is detected
    )

    # Decode the generated token IDs back to human-readable text
    result = tokenizer.decode(output[0], skip_special_tokens=True)

    # Remove the original prompt to isolate the bot's answer
    answer = result.replace(prompt, "").strip()

    # Print the answer
    print(f"Bot: {answer}\n")
