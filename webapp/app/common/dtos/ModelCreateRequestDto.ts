export interface ModelCreateRequestDto {
    warehouseId: number;
    modelName:  string;
    modelDetails:{
       modelType: string;
       sourceQuery: string;
    };
    queryModelPK:{
       primaryKeys: string[];
    };
 }