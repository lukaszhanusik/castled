export interface ModelCreateRequestDto {
    warehouseId: number;
    modelName:  string;
    modelType:  string;
    modelDetails:{
       type: string;
       sourceQuery: string;
    };
    queryModelPK:{
       primaryKeys: string[];
    };
 }