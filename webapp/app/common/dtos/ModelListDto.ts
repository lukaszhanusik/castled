export interface ModelListDto {
    warehouseId: number;
    modelName:  string;
    modelDetails:{
       modelType: string,
       sourceQuery: string,
    };
    queryModelPK:{
       primaryKeys: string[]
    }
 }