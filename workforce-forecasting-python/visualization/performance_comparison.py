import matplotlib.pyplot as plt
import numpy as np
import pandas as pd
import os

def load_actual_metrics():
    """Load actual metrics from model_comparison.csv if it exists"""
    csv_path = 'results/model_comparison.csv'
    if os.path.exists(csv_path):
        df = pd.read_csv(csv_path)
        # Convert to dictionary format
        metrics_dict = {}
        for _, row in df.iterrows():
            model_name = row['Model']
            metrics_dict[model_name] = {
                'RMSE': row['RMSE'],
                'MAE': row['MAE'],
                'MAPE': row['MAPE'],
                'R²': row['R2']
            }
        return metrics_dict
    else:
        print(f"Warning: {csv_path} not found. Using placeholder values.")
        print("Please train the models first to generate actual metrics.")
        return None

def create_performance_comparison_chart(metrics_dict=None):
    """Create performance comparison bar chart"""
    
    # Use actual metrics if provided, otherwise use placeholders
    if metrics_dict is None:
        metrics_dict = {
            'Linear Regression': {
                'RMSE': 12.45,
                'MAE': 9.82,
                'MAPE': 8.35,
                'R²': 0.78
            },
            'Random Forest': {
                'RMSE': 8.32,
                'MAE': 6.15,
                'MAPE': 5.42,
                'R²': 0.89
            },
            'XGBoost': {
                'RMSE': 6.78,
                'MAE': 5.23,
                'MAPE': 4.56,
                'R²': 0.92
            },
            'LSTM': {
                'RMSE': 7.15,
                'MAE': 5.89,
                'MAPE': 5.12,
                'R²': 0.91
            }
        }
    
    # Convert to DataFrame
    df = pd.DataFrame(metrics_dict).T
    
    # Create figure with subplots
    fig, axes = plt.subplots(2, 2, figsize=(14, 10))
    fig.suptitle('Figure 4.12 – Performance Comparison of Machine Learning Models', 
                 fontsize=16, fontweight='bold', y=0.98)
    
    # Flatten axes for easy iteration
    axes = axes.flatten()
    
    # Metrics to plot
    metrics = ['RMSE', 'MAE', 'MAPE', 'R²']
    colors = ['#3498db', '#2ecc71', '#e74c3c', '#f39c12']
    titles = [
        'Root Mean Square Error (Lower is Better)',
        'Mean Absolute Error (Lower is Better)',
        'Mean Absolute Percentage Error % (Lower is Better)',
        'R-Squared Score (Higher is Better)'
    ]
    
    # Plot each metric
    for idx, (metric, ax, color, title) in enumerate(zip(metrics, axes, colors, titles)):
        values = df[metric].values
        models = df.index.tolist()
        
        bars = ax.bar(models, values, color=color, alpha=0.8, edgecolor='black', linewidth=1.5)
        
        # Add value labels on bars
        for bar, val in zip(bars, values):
            height = bar.get_height()
            ax.text(bar.get_x() + bar.get_width()/2., height,
                    f'{val:.2f}',
                    ha='center', va='bottom', fontweight='bold', fontsize=11)
        
        ax.set_ylabel(metric, fontsize=12, fontweight='bold')
        ax.set_title(title, fontsize=11, pad=10)
        ax.set_xlabel('Machine Learning Models', fontsize=12, fontweight='bold')
        ax.grid(axis='y', alpha=0.3, linestyle='--')
        ax.set_ylim(bottom=0)
        
        # Rotate x-axis labels for better readability
        ax.tick_params(axis='x', rotation=0, labelsize=11)
        ax.tick_params(axis='y', labelsize=10)
    
    plt.tight_layout(rect=[0, 0, 1, 0.96])
    
    # Save the figure
    os.makedirs('results', exist_ok=True)
    output_path = 'results/performance_comparison.png'
    plt.savefig(output_path, dpi=300, bbox_inches='tight')
    print(f"Performance comparison chart saved to: {output_path}")
    
    # Also save as PDF for thesis
    pdf_path = 'results/performance_comparison.pdf'
    plt.savefig(pdf_path, format='pdf', bbox_inches='tight')
    print(f"Performance comparison chart saved to: {pdf_path}")
    
    plt.show()
    
    # Print metrics table
    print("\n" + "="*80)
    print("MODEL PERFORMANCE METRICS")
    print("="*80)
    print(df.to_string())
    print("="*80)
    
    # Save metrics to CSV
    df.to_csv('results/model_performance_metrics.csv', index=True)
    print("\nMetrics saved to: results/model_performance_metrics.csv")

if __name__ == "__main__":
    # Try to load actual metrics from training results
    actual_metrics = load_actual_metrics()
    create_performance_comparison_chart(actual_metrics)
