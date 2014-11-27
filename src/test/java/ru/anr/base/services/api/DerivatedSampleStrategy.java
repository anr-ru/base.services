/**
 * 
 */
package ru.anr.base.services.api;

import ru.anr.base.domain.api.APICommand;
import ru.anr.base.domain.api.MethodTypes;
import ru.anr.base.domain.api.models.ResponseModel;

/**
 * S sample strategy to show overriding of methods.
 *
 *
 * @author Alexey Romanchuk
 * @created Nov 14, 2014
 *
 */

public class DerivatedSampleStrategy extends SampeStrategy {

    /**
     * {@inheritDoc}
     */
    @Override
    @ApiMethod(MethodTypes.Post)
    public ResponseModel post(APICommand cmd) {

        ResponseModel m = new ResponseModel();
        m.setCode(100);

        return m;
    }
}
