import pandas as pd

df = pd.read_csv("ai.csv")

print(df.info())
print(df.head())

missing_values = df.isnull().sum()
missing_percentage = (missing_values / len(df)) * 100

print(pd.DataFrame({"Missing Values": missing_values, "Percentage": missing_percentage}))

df['Street Address'] = df['Street Address'].fillna(df['Street Address'].mode()[0])
df['City'] = df['City'].fillna(df['City'].mode()[0])
df['Postcode'] = df['Postcode'].fillna(df['Postcode'].mode()[0])
df['Phone Number'] = df['Phone Number'].fillna("Unknown")
df['Longitude'] = df['Longitude'].fillna(df['Longitude'].mean())
df['Latitude'] = df['Latitude'].fillna(df['Latitude'].mean())
print(df.isna().sum())
df.to_csv(r"C:\Users\celin\PycharmProjects\task2\ai.csv", index=False)



