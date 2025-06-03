import numpy as np
import pandas as pd
from matplotlib import pyplot as plt
from sklearn.compose import ColumnTransformer
from sklearn.linear_model import LinearRegression
from sklearn.model_selection import train_test_split
from sklearn.metrics import mean_squared_error, r2_score
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import StandardScaler, OneHotEncoder


df_og = pd.read_csv("ai3.csv")
df = df_og.copy()

lower_bound_duration = df['duration'].quantile(0.01)
upper_bound_duration = df['duration'].quantile(0.99)
df['duration'] = df['duration'].clip(lower=lower_bound_duration, upper=upper_bound_duration)

lower_bound_votes = df['votes'].quantile(0.01)
upper_bound_votes = df['votes'].quantile(0.99)
df['votes'] = df['votes'].clip(lower=lower_bound_votes, upper=upper_bound_votes)

df['votes_log'] = np.log1p(df['votes'])
df.drop('votes', axis=1, inplace=True)
df.rename(columns={'votes_log': 'votes'}, inplace=True)

df['decade'] = (df['year'] // 10) * 10

df['age'] = 2025 - df['year']

df['lead_actor'] = df['stars'].str.split(',').str[0]

X = df[['year', 'duration', 'votes', 'decade', 'age', 'genre', 'certificate', 'lead_actor']]
y = df['rating']

categorical_features = ['genre', 'certificate', 'lead_actor']
numeric_features = ['year', 'duration', 'votes', 'decade', 'age']

preprocessor = ColumnTransformer(
    transformers=[
        ('num', StandardScaler(), numeric_features),
        ('cat', OneHotEncoder(handle_unknown='ignore'), categorical_features)
    ])

model = Pipeline(steps=[
    ('preprocessor', preprocessor),
    ('regressor', LinearRegression())
])

X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)

model.fit(X_train, y_train)

y_pred = model.predict(X_test)

print("Mean Squared Error:", mean_squared_error(y_test, y_pred))
print("R^2 Score:", r2_score(y_test, y_pred))

plt.figure(figsize=(8, 6))
plt.scatter(y_test, y_pred, color='blue', alpha=0.5)
plt.plot([y_test.min(), y_test.max()], [y_test.min(), y_test.max()], color='red', linewidth=2)
plt.title('Actual vs Predicted Ratings')
plt.xlabel('Actual Ratings')
plt.ylabel('Predicted Ratings')
plt.grid(True)
plt.show()