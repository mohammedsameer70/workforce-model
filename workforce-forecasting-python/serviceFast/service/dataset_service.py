import pandas as pd
import numpy as np
import logging

logger = logging.getLogger(__name__)


class DatasetService:

    def read_csv(self, df):

        try:

            logger.info("Inside DatasetService")

            df = df.head()

            numeric_columns = df.select_dtypes(include=[np.number]).columns

            df[numeric_columns] = df[numeric_columns].replace([np.inf, -np.inf], None)

            df = df.where(pd.notnull(df), None)

            result = df.to_dict(orient="records")

            logger.info(f"Returning {len(result)} records")

            return result

        except Exception as e:

            logger.exception("DatasetService Error")

            raise Exception(str(e))
